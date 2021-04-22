(ns dinsro.ui.index-categories-test
  (:require
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-categories :as u.index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexCategories
  {::wsm/card-height 3
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-categories/IndexCategories
    ::ct.fulcro3/initial-state
    (fn []
      {::u.index-categories/categories
       (map
        (fn [category]
          (let [user-id (::m.users/id (::m.categories/user category))
                user    (get sample/user-map user-id)]
            {::m.categories/id   (::m.categories/id category)
             ::m.categories/name (::m.categories/name category)
             ::m.categories/user {::m.users/id   user-id
                                  ::m.users/name (::m.users/name user)}}))
        (map sample/category-map [1 2 3]))})}))
