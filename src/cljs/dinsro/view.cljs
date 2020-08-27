(ns dinsro.view
  (:require
   [com.smxemail.re-frame-document-fx]
   [dinsro.components.navbar :refer [navbar]]
   [dinsro.routing :as r]
   [dinsro.store.reframe :refer [reframe-store]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(defn root-component []
  (let [store (reframe-store)
        route-defs (->> r/mappings
                        (map identity)
                        (map (fn [[k view-handler]] [k (partial view-handler store)]))
                        (into [])
                        (reduce concat []))
        routes (concat [nil [:div "Not Found"]]
                       route-defs)]
    [:<>
     [navbar]
     (into [kf/switch-route #(get-in % [:data :name])]
           routes)]))
