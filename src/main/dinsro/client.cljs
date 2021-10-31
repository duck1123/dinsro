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
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as ui]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.login :as u.login]
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
        (routing/route-to! app u.login/LoginPage {})
        (hist5/restore-route! app u.home/HomePage {})))))

(defn setup-RAD [app]
  (let [all-controls (u.controls/all-controls)]
    (rad-app/install-ui-controls! app all-controls))
  (report/install-formatter! app :boolean :affirmation (fn [_ value] (if value "yes" "no"))))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (log/merge-config! {:output-fn prefix-output-fn
                      :appenders {:console (console-appender)}})
  (app/set-root! da/app ui/Root {:initialize-state? true})
  (setup-RAD da/app)
  (dr/change-route! da/app [""])
  (history/install-route-history! da/app (html5-history))
  (auth/start! da/app [u.login/LoginPage] {:after-session-check `fix-route})
  (app/mount! da/app ui/Root "app" {:initialize-state? false})
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  (rad-app/install-ui-controls! da/app sui/all-controls)
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! da/app ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! da/app)
  (setup-RAD da/app)
  (js/console.log "Hot reload"))

(defonce performance-stats (tufte/add-accumulating-handler! {}))

(defn pperf
  "Dump the currently-collected performance stats"
  []
  (let [stats (not-empty @performance-stats)]
    (println (tufte/format-grouped-pstats stats))))
