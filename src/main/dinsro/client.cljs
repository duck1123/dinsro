(ns dinsro.client
  (:require
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.mutations :as m]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.application :as rad-app]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.routing :as routing]
   [com.fulcrologic.rad.routing.history :as history]
   [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
   [dinsro.app :as da]
   [dinsro.loader]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as ui]
   [dinsro.ui.login-dialog :refer [LoginForm]]
   [taoensso.timbre :as log]
   [taoensso.tufte :as tufte]))

(defonce stats-accumulator
  (tufte/add-accumulating-handler! {:ns-pattern "*"}))

(m/defmutation fix-route
  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
  [_]
  (action [{:keys [app]}]
          (let [logged-in (auth/verified-authorities app)]
            (if (empty? logged-in)
              (routing/route-to! app ui/LandingPage {})
              (hist5/restore-route! app ui/LandingPage {})))))

(defn setup-RAD [app]
  (rad-app/install-ui-controls! app sui/all-controls)
  (report/install-formatter! app :boolean :affirmation (fn [_ value] (if value "yes" "no"))))

(defonce app (rad-app/fulcro-rad-app {}))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (log/merge-config! {:output-fn prefix-output-fn
                      :appenders {:console (console-appender)}})
  (app/set-root! app ui/Root {:initialize-state? true})
  (dr/initialize! app)
  (setup-RAD app)
  (dr/change-route! app ["landing-page"])
  (history/install-route-history! app (html5-history))
  (auth/start! app [LoginForm] {:after-session-check `fix-route})
  (app/mount! app ui/Root "app" {:initialize-state? false})

  ;; (app/mount! da/app Root "app" {:initialize-state? false})
  ;; (routing/start!)
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! da/app ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! da/app)
  (js/console.log "Hot reload"))

(defonce performance-stats (tufte/add-accumulating-handler! {}))

(defn pperf
  "Dump the currently-collected performance stats"
  []
  (let [stats (not-empty @performance-stats)]
    (println (tufte/format-grouped-pstats stats))))
