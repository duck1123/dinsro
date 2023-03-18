(ns dinsro.env
  (:require
   [dinsro.dev-middleware :refer [wrap-dev]]
   [lambdaisland.glogc :as log]
   [selmer.parser :as parser]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info :defaults/starting {}))
   :stop
   (fn []
     (log/info :defaults/stopping {}))
   :middleware wrap-dev})
