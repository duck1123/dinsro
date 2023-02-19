(ns dinsro.ui.categories
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

;; [[../joins/categories.cljc][Category Joins]]
;; [[../model/categories.cljc][Categories Model]]

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

(def override-admin-form true)

(form/defsc-form AdminCategoryForm
  [this props]
  {fo/attributes    [m.categories/name m.categories/user]
   fo/cancel-route  ["admin"]
   fo/field-options {::m.categories/user {::picker-options/query-key       ::m.users/index
                                          ::picker-options/query-component u.links/UserLinkForm
                                          ::picker-options/options-xform
                                          (fn [_ options]
                                            (mapv
                                             (fn [{::m.users/keys [id name]}]
                                               {:text  (str name)
                                                :value [::m.users/id id]})
                                             (sort-by ::m.users/name options)))}}
   fo/field-styles  {::m.categories/user :pick-one}
   fo/id            m.categories/id
   fo/route-prefix  "admin/category"
   fo/title         "Category"}
  (if override-admin-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))

(def new-button
  {:action (fn [this _] (form/create! this NewForm))
   :label  "New"
   :local? true
   :type   :button})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.categories/name
                        m.categories/user]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/field-formatters {::m.categories/user #(u.links/ui-user-link %2)}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/route            "categories"
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.categories/index
   ro/title            "Categories"})

(report/defsc-report CategoriesSubReport
  [_this _props]
  {ro/field-formatters {::m.categories/user #(u.links/ui-user-link %2)}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/columns          [m.categories/name]
   ro/row-actions      []
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.categories/index
   ro/title            "Categories"})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.categories/name m.categories/user]
   ro/controls         {::new {:label  "New Category"
                               :type   :button
                               :action #(form/create! % AdminCategoryForm)}}
   ro/field-formatters {::m.categories/user #(u.links/ui-user-link %2)}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/source-attribute ::j.categories/admin-index
   ro/title            "Admin Categories"
   ro/row-pk           m.categories/id
   ro/route            "categories"
   ro/run-on-mount?    true})
