(ns dinsro.mutations.navlinks
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as dt])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.navlinks :as m.navlinks]
   [lambdaisland.glogc :as log]))

;; [[../joins/navlinks.cljc]]
;; [[../model/navlinks.cljc]]
;; [[../ui/navlinks.cljs]]

#?(:clj (comment ::dr/_ ::m.navlinks/id))
#?(:cljs (comment ::dr/_))

(defn update-route-id
  "Add the route id to the page map"
  [state target model-key record-id]
  (let [[page-db page-id] target]
    (log/trace :update-route-id/starting
      {:page-id   page-id
       :record-id record-id
       :model-key model-key})
    (swap! state assoc-in [page-db page-id model-key] record-id)))

#?(:cljs
   (defmutation target-ready [props]
     (action [{:keys [app state]}]
       (let [{:keys [model-key record-id page-id]} props
             target [::m.navlinks/id page-id]]
         (log/trace :target-ready/starting
           {:page-id   page-id
            :record-id record-id
            :props     props})

         ;; Store the current page to :root/current-page
         (swap! state #(dt/integrate-ident* % target :replace [:root/current-page]))

         (if (and model-key record-id)
           (do
             (log/debug :target-ready/id-found
               {:page-id   page-id
                :record-id record-id
                :model-key model-key
                :props     props})
             (update-route-id state target  model-key record-id))
           (do
             (log/debug :target-ready/no-id {:props props})
             nil))
         (dr/target-ready! app target)))))

#?(:cljs
   (defmutation routing-target-ready [props]
     (action [{:keys [app state]}]
       (let [{:keys [model-key record-id page-id]} props
             target [::m.navlinks/id page-id]]
         (if (and model-key record-id)
           (do
             (log/debug :routing-target-ready/id
               {:page-id   page-id
                :record-id record-id
                :model-key model-key
                :props     props})
             (update-route-id state target model-key record-id))
           (do
             (log/debug :routing-target-ready/no-id {:page-id page-id :props props})
             nil))
         (dr/target-ready! app target)))))
