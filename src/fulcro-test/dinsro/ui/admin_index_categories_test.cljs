(ns dinsro.ui.admin-index-categories-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexCategories
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-categories/AdminIndexCategories
    ::ct.fulcro3/initial-state
    (fn [] {:categories [{:currency-id 1
                          :category/name "foo"
                          :category/user-id 1}
                         {:currency-id 2
                          :category/name "bar"
                          :category/user-id 2}]})
    ::ct.fulcro3/wrap-root? false}))
