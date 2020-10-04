(ns dinsro.view
  (:require
   [com.smxemail.re-frame-document-fx]
   [dinsro.components.navbar :refer [navbar]]
   [dinsro.mappings :refer [mappings]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(defn root-component [store]
  (let [route-defs (->> mappings
                        (map identity)
                        (map (fn [[k view-handler]] [k (partial view-handler store)]))
                        (into [])
                        (reduce concat []))
        routes (concat [nil [:div "Not Found"]]
                       route-defs)]
    [:<>
     [navbar store]
     (into [kf/switch-route #(get-in % [:data :name])]
           routes)]))
