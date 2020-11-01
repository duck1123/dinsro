(ns dinsro.ui.index-categories-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-categories :as u.index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexCategories
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-categories/IndexCategories
    ::ct.fulcro3/initial-state
    (fn [] {:categories/list [{:user/id 1
                               :user/name "Foo"}
                              {:user/id 1
                               :user/name "Bar"}
                              {:user/id 2
                               :user/name "Baz"}]})
    ::ct.fulcro3/wrap-root? false}))
