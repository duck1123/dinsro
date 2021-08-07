(ns dinsro.client
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.mutations :as m]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.routing :as routing]
   [com.fulcrologic.rad.routing.html5-history :as hist5]
   [dinsro.app :as da]
   [dinsro.routing :as d.routing]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as ui]
   [dinsro.views.home :as v.home]
   [dinsro.views.login :as v.login]
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
        (routing/route-to! app v.login/LoginPage {})
        (hist5/restore-route! app v.home/HomePage {})))))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/set-root! da/app ui/Root {:initialize-state? true})
  (auth/start! da/app [v.login/LoginPage] {:after-session-check `fix-route})
  (app/mount! da/app ui/Root "app" {:initialize-state? false})
  (d.routing/start!)
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
