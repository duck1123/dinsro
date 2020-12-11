(ns dinsro.views.index-categories-test
  (:require
   [dinsro.views.index-categories :as v.index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexCategoriesPage
  {::wsm/align {:flex 1}
   ::wsm/card-height 10
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-categories/IndexCategoriesPage
    ::ct.fulcro3/initial-state
    (fn [] {:button-data {}
            :form-data {}
            :categories {}})}))
