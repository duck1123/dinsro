(ns dinsro.ui.admin.categories
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.categories :as j.categories]
   [dinsro.joins.users :as j.users]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]))

(def override-admin-form true)

(def user-picker
  {::picker-options/query-key       ::j.users/admin-index-flat
   ::picker-options/query-component u.links/UserLinkForm
   ::picker-options/options-xform
   (fn [_ options]
     (mapv
      (fn [{::m.users/keys [id name]}]
        {:text  (str name)
         :value [::m.users/id id]})
      (sort-by ::m.users/name options)))})

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.categories/name m.categories/user]
   fo/cancel-route  ["admin"]
   fo/field-options {::m.categories/user user-picker}
   fo/field-styles  {::m.categories/user :pick-one}
   fo/id            m.categories/id
   fo/route-prefix  "new-category"
   fo/title         "Category"}
  (if override-admin-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))

(def new-button
  {:label  "New Category"
   :type   :button
   :action #(form/create! % NewForm)})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.categories/user #(u.links/ui-user-link %2)}
   ro/columns           [m.categories/name
                         m.categories/user
                         j.categories/transaction-count]
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/form-links        {::m.categories/name NewForm}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.categories/id mu.categories/delete!)]
   ro/row-pk            m.categories/id
   ro/route             "categories"
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/admin-index
   ro/title             "Categories"})
