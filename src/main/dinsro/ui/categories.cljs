(ns dinsro.ui.categories
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(form/defsc-form NewCategoryForm
  [_this _props]
  {fo/id           m.categories/id
   fo/attributes   [m.categories/name]
   fo/subforms     {::m.categories/user {fo/ui u.links/UserLinkForm}}
   fo/cancel-route ["categories"]
   fo/route-prefix "new-category"
   fo/title        "New Category"
   fo/field-styles {::m.categories/user :link}})

(def override-form true)

(form/defsc-form CategoryForm
  [this props]
  {fo/id           m.categories/id
   fo/attributes   [m.categories/name
                    m.categories/user]
   fo/subforms     {::m.categories/user {fo/ui u.links/UserLinkForm}}
   fo/cancel-route ["categories"]
   fo/route-prefix "category"
   fo/title        "Edit Category"
   fo/field-styles {::m.categories/user :link}}
  (if override-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))

(def override-admin-form true)

(form/defsc-form AdminCategoryForm
  [this props]
  {fo/id            m.categories/id
   fo/attributes    [m.categories/name m.categories/user]
   fo/field-options {::m.categories/user {::picker-options/query-key       ::m.users/index
                                          ::picker-options/query-component u.links/UserLinkForm
                                          ::picker-options/options-xform
                                          (fn [_ options]
                                            (mapv
                                             (fn [{::m.users/keys [id name]}]
                                               {:text  (str name)
                                                :value [::m.users/id id]})
                                             (sort-by ::m.users/name options)))}}
   fo/cancel-route  ["admin"]
   fo/route-prefix  "admin/category"
   fo/field-styles  {::m.categories/user :pick-one}
   fo/title         "Category"}
  (if override-admin-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewCategoryForm))})

(report/defsc-report CategoriesReport
  [_this _props]
  {ro/field-formatters {::m.categories/user (fn [_this props] (u.links/ui-user-link props))}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/columns          [m.categories/name
                        m.categories/user]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/route            "categories"
   ro/row-actions      []
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.categories/index
   ro/title            "Categories"})

(report/defsc-report CategoriesSubReport
  [_this _props]
  {ro/field-formatters {::m.categories/user (fn [_this props] (u.links/ui-user-link props))}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/columns          [m.categories/name]
   ro/row-actions      []
   ro/row-pk           m.categories/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.categories/index
   ro/title            "Categories"})

(report/defsc-report AdminIndexCategoriesReport
  [_this _props]
  {ro/columns          [m.categories/name m.categories/user]
   ro/controls         {::new {:label  "New Category"
                               :type   :button
                               :action #(form/create! % AdminCategoryForm)}}
   ro/field-formatters {::m.categories/user (fn [_this props] (u.links/ui-user-link props))}
   ro/form-links       {::m.categories/name CategoryForm}
   ro/source-attribute ::m.categories/admin-index
   ro/title            "Admin Categories"
   ro/row-pk           m.categories/id
   ro/route            "categories"
   ro/run-on-mount?    true})
