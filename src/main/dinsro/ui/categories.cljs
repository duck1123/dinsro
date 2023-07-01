(ns dinsro.ui.categories
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.ui.links :as u.links]))

;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]
;; [[../ui/admin/users/categories.cljs]]

(def model-key ::m.categories/id)
(def show-page-key :categories-show)

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.categories/name]
   fo/cancel-route ["categories"]
   fo/field-styles {::m.categories/user :link}
   fo/id           m.categories/id
   fo/route-prefix "new-category"
   fo/title        "New Category"})

(def override-form true)

(form/defsc-form CategoryForm
  [this props]
  {fo/attributes   [m.categories/name
                    m.categories/user]
   fo/cancel-route ["categories"]
   fo/field-styles {::m.categories/user :link}
   fo/id           m.categories/id
   fo/route-prefix "category"
   fo/title        "Edit Category"}
  (if override-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.categories/user #(u.links/ui-user-link %2)}
   ro/columns           [m.categories/name
                         m.categories/user]
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/form-links        {::m.categories/name CategoryForm}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "categories"
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/index
   ro/title             "Categories"})
