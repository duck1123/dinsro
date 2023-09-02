(ns dinsro.mutations.menus
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [dinsro.options.navbars :as o.navbars]
   #?(:cljs [lambdaisland.glogc :as log])))

(def model-key o.navbars/id)

(def menu-loaded?
  "Flag specifying that the menu for this item has been loaded"
  :ui/menu-loaded?)

#?(:clj (comment model-key))

#?(:cljs
   (fm/defmutation loaded [props]
     (action [{:keys [state]}]
       (if-let [id (:id props)]
         (do
           (log/trace :loaded/starting {:id id :props props})
           (swap! state assoc-in [model-key id menu-loaded?] true)
           true)
         (do
           (log/warn :loaded/no-id {:props props})
           false)))))
