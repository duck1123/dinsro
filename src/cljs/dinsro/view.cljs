(ns dinsro.view
  (:require [com.smxemail.re-frame-document-fx]
            [dinsro.components.navbar :refer [navbar]]
            [dinsro.routing :as r]
            [kee-frame.core :as kf]))

(defn root-component []
  [:<>
   [navbar]
   (into [kf/switch-route #(get-in % [:data :name])]
         (concat [nil [:div "Not Found"]]
                 (->> r/mappings
                      (map identity)
                      (into [])
                      (reduce concat []))))])
