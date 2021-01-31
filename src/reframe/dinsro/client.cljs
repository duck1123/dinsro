(ns dinsro.client
  (:require
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn ^:export start
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (js/console.log "Loaded"))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  (js/console.log "Hot reload"))
