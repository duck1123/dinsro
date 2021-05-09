(ns dinsro.client
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.app :as da]
   [dinsro.loader]
   [dinsro.routing :as routing]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as ui]
   [taoensso.timbre :as log]))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function.
  See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/set-root! da/app ui/Root {:initialize-state? true})
  (app/mount! da/app ui/Root "app" {:initialize-state? false})
  (routing/start!)
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;; re-mounting will cause forced UI refresh, update internals, etc.
  (app/mount! da/app ui/Root "app")
  ;; As of Fulcro 3.3.0, this addition will help with stale queries when using dynamic routing:
  (comp/refresh-dynamic-queries! da/app)
  (js/console.log "Hot reload"))
