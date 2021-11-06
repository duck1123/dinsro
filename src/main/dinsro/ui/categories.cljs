(ns dinsro.ui.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defattr user-link ::m.categories/user :ref
  {ao/cardinality      :one
   ao/identities       #{::m.categories/id}
   ao/target           ::m.users/id
   ::report/column-EQL {::m.categories/user (comp/get-query u.links/UserLink)}})

(def override-form true)

(form/defsc-form CategoryForm
  [this props]
  {fo/id           m.categories/id
   fo/attributes   [m.categories/name m.categories/user]
   fo/subforms     {::m.categories/user {fo/ui u.links/UserLinkForm}}
   fo/cancel-route ["categories"]
   fo/route-prefix "category"
   fo/title        "Edit Category"}
  (if override-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))

(report/defsc-report CategoriesReport
  [_this _props]
  {ro/field-formatters {::m.categories/user (fn [_this props] (u.links/ui-user-link props))}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/columns          [m.categories/name user-link]
   ro/route            "categories"
   ro/row-actions      []
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.categories/all-categories
   ro/title            "Categories"})

(report/defsc-report AdminIndexCategoriesReport
  [_this _props]
  {ro/columns          [m.categories/name]
   ro/source-attribute ::m.categories/all-categories
   ro/title            "Categories"
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true})
