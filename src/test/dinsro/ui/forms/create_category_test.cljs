(ns dinsro.ui.forms.create-category-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard CreateCategoryForm
  {::wsm/card-height 5
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.create-category/CreateCategoryForm
    ::ct.fulcro3/initial-state
    (fn [] {::u.f.create-category/name ""})}))
