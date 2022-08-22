(ns dinsro.env
  (:require
   [dinsro.dev-middleware :refer [wrap-dev]]
   [selmer.parser :as parser]
   [lambdaisland.glogc :as log]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info :defaults/starting {}))
   :stop
   (fn []
     (log/info :defaults/stopping {}))
   :middleware wrap-dev})
