(ns dinsro.ui.pickers
  (:require
   [com.fulcrologic.rad.picker-options :as picker-options]
   [dinsro.joins.core.nodes :as j.c.nodes]
   [dinsro.joins.users :as j.users]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(def network-picker
  {::picker-options/query-key       ::j.c.nodes/index
   ::picker-options/query-component u.links/NetworkLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.c.networks/keys [id name]}]
        {:text  (str name)
         :value [::m.c.networks/id id]})

      (sort-by ::m.c.networks/name options)))})

(def node-picker
  {::picker-options/query-key       ::j.c.nodes/index
   ::picker-options/query-component u.links/CoreNodeLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.c.nodes/keys [id name]}]
        {:text  (str name)
         :value [::m.c.nodes/id id]})
      (sort-by ::m.c.nodes/name options)))})

(def user-picker
  {::picker-options/query-key       ::j.users/index
   ::picker-options/query-component u.links/UserLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.users/keys [id name]}]
        {:text  (str name)
         :value [::m.users/id id]})
      (sort-by ::m.users/name options)))})
