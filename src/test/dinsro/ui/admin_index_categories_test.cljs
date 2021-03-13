(ns dinsro.ui.admin-index-categories-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(def categories (map sample/category-map [1 2]))

(ws/defcard AdminIndexCategories
  {::wsm/align       {:flex 1}
   ::wsm/card-height 8
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-categories/AdminIndexCategories
    ::ct.fulcro3/initial-state
    (fn [] {::u.admin-index-categories/categories categories})}))
