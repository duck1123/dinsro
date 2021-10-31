(ns dinsro.ui.admin-index-categories-test
  (:require
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AdminIndexCategories
  {::wsm/align       {:flex 1}
   ::wsm/card-height 9
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-categories/AdminIndexCategories
    ::ct.fulcro3/initial-state
    (fn []
      (let [category-id (new-uuid)
            user-id     (new-uuid)
            user        {::m.users/id   user-id
                         ::m.users/name m.users/default-username}
            category    {::m.categories/id   category-id
                         ::m.categories/name "A"}]
        {::u.admin-index-categories/categories
         [{::m.categories/id   category-id
           ::m.categories/link category
           ::m.categories/user user}]}))}))
