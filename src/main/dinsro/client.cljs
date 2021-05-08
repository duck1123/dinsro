(ns dinsro.client
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.app :as da]
   [dinsro.machines :as machines]
   [dinsro.loader]
   [dinsro.router :as router]
   [dinsro.routing :as routing]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.navbar :as u.navbar]
   [taoensso.timbre :as timbre]))

(defsc Root [this {::keys [navbar router]}]
  {:query         [{::navbar (comp/get-query u.navbar/Navbar)}
                   {::router (comp/get-query router/RootRouter)}]
   :initial-state {::navbar {}
                   ::router {}}}
  (let [top-router-state (or (uism/get-active-state this ::router/RootRouter) :initial)]
    (dom/div {}
      (u.navbar/ui-navbar navbar)
      (dom/div :.ui.container
        (if (= :initial top-router-state)
          (dom/div :.loading "Loading...")
          (router/ui-root-router router))))))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/set-root! da/app Root {:initialize-state? true})
  (app/mount! da/app Root "app" {:initialize-state? false})
  (routing/start!)
  (uism/begin! da/app machines/hideable ::navbarsm {:actor/navbar u.navbar/Navbar})
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! da/app Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! da/app)
  (js/console.log "Hot reload"))
