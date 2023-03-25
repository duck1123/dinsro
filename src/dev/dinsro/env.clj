(ns dinsro.env
  (:require
   [lambdaisland.glogc :as log]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def defaults
  {:init
   (fn []
     (log/info :defaults/starting {}))
   :stop
   (fn []
     (log/info :defaults/stopping {}))
   :middleware identity})
