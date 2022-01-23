(ns dinsro.client
  (:require
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as m]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.application :as rad-app]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.routing :as routing]
   [com.fulcrologic.rad.routing.history :as history]
   [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
   [dinsro.formatters.date-time :as fmt.date-time]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as ui]
   [dinsro.ui.admin :as u.admin]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.login :as u.login]
   [dinsro.ui.users :as u.users]
   [taoensso.timbre :as log]
   [taoensso.tufte :as tufte]))

(m/defmutation ui-ready
  "Mutation. Called at the end of [[init]], after `dr/initialize!` and thus 'executed' after all relevant routers have been started"
  [_]
  (action [{:keys [state]}]
    (swap! state assoc :ui/ready? true)
    (log/info "UI ready!")))

(defn restore-route-ensuring-leaf!
  "Attempt to restore the route given in the URL. If that fails, simply route to the default given (a class and map).
   WARNING: This should not be called until the HTML5 history is installed in your app.
   (Based on `hist5/restore-route!` modified to check for Partial Routes and routing to the correct leaf target.)

   NOTE: Fulcro dyn. routing requires that you always route to a leaf target, i.e. not just to a router somewhere in
   the middle of your UI tree with some unrouted, descendant routers - otherwise weird stuff may happen."
  [app]
  (let [{:keys [route params]} (hist5/url->route)
        target0                (dr/resolve-target app route)
        target                 (condp = target0
                                 nil               u.home/HomePage
                                 u.admin/AdminPage u.users/AdminIndexUsersReport
                                 target0)]
    (routing/route-to! app target (or params {}))))

(defonce app
  (rad-app/fulcro-rad-app
   {:client-did-mount
    (fn [app]
      (log/merge-config! {:output-fn prefix-output-fn
                          :appenders {:console (console-appender)}})
      (restore-route-ensuring-leaf! app))}))

(defonce stats-accumulator
  (tufte/add-accumulating-handler! {:ns-pattern "*"}))

(m/defmutation fix-route
  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
  [_]
  (action [{:keys [app]}]
    (let [logged-in (auth/verified-authorities app)]
      (if (empty? logged-in)
        (hist5/restore-route! app u.home/HomePage {})
        (hist5/restore-route! app u.home/HomePage {})))))

(defn setup-RAD [app]
  (let [all-controls (u.controls/all-controls)]
    (rad-app/install-ui-controls! app all-controls))
  (report/install-formatter! app :inst :default fmt.date-time/date-formatter)
  (report/install-formatter! app :boolean :affirmation (fn [_ value] (if value "yes" "no"))))

(def my-auth-machine
  (-> auth/auth-machine
      (assoc-in
       [::uism/states
        :state/gathering-credentials
        ::uism/events
        :event/cancel]
       {::uism/target-state :state/idle})
      (assoc-in
       [::uism/states
        :state/idle
        ::uism/events
        :event/cancel]
       {::uism/target-state :state/idle})))

(uism/register-state-machine! `auth/auth-machine my-auth-machine)

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/set-root! app ui/Root {:initialize-state? true})
  (dr/change-route! app [""])
  (history/install-route-history! app (html5-history))
  (setup-RAD app)

  (set! eb/*render-error* (fn [this error]
                            (println this)
                            (println error)
                            (dom/div "Error")))

  (auth/start! app [u.login/LoginPage] {:after-session-check `fix-route})
  (app/mount! app ui/Root "app" {:initialize-state? false})
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  (log/info "refresh")
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! app ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! app)
  (setup-RAD app)
  (js/console.log "Hot reload"))

(defonce performance-stats (tufte/add-accumulating-handler! {}))

(defn pperf
  "Dump the currently-collected performance stats"
  []
  (let [stats (not-empty @performance-stats)]
    (println (tufte/format-grouped-pstats stats))))
