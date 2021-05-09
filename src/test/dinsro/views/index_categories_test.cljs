(ns dinsro.views.index-categories-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.user-categories :as u.user-categories]
   [dinsro.views.index-categories :as v.index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexCategoriesPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 9
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-categories/IndexCategoriesPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.index-categories/button-data {}
       ::v.index-categories/categories
       {::u.user-categories/categories {::u.user-categories/categories (vals sample/category-map)}}
       ::v.index-categories/form-data   {}})}))
