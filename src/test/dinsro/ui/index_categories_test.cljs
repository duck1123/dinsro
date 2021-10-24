(ns dinsro.ui.index-categories-test
  (:require
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-categories :as u.index-categories]
   [dinsro.ui.user-categories :as u.user-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexCategories
  {::wsm/card-height 6
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-categories/IndexCategories
    ::ct.fulcro3/initial-state
    (fn []
      {::u.index-categories/categories
       (map
        (fn [category]
          (let [user {::m.users/id   (new-uuid)
                      ::m.users/name "bob"}]
            {::m.categories/id   (new-uuid)
             ::m.categories/name (::m.categories/name category)
             ::m.categories/user user}))
        (vals sample/category-map))})}))

(ws/defcard IndexCategoriesPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 9
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-categories/IndexCategoriesPage
    ::ct.fulcro3/initial-state
    (fn []
      {::u.index-categories/button-data {}
       ::u.index-categories/categories
       {::u.user-categories/categories {::u.user-categories/categories (vals sample/category-map)}}
       ::u.index-categories/form-data   {}})}))
