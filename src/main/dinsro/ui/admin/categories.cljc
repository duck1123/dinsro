(ns dinsro.ui.admin.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.categories :as j.categories]
   [dinsro.joins.users :as j.users]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/categories.cljc]]
;; [[../../model/categories.cljc]]
;; [[../../ui/categories.cljs]]

(def index-page-id :admin-categories)
(def model-key ::m.categories/id)
(def override-admin-form true)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-key :admin-categories-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.categories/delete!))

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
  {ro/column-formatters {::m.categories/name #(u.links/ui-admin-category-link %3)
                         ::m.categories/user #(u.links/ui-admin-user-link %2)}
   ro/columns           [m.categories/name
                         m.categories/user
                         j.categories/transaction-count]
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/admin-index
   ro/title             "Categories"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.categories/keys [id name]
          :as                 props}]
  {:ident          ::m.categories/id
   :initial-state  {::m.categories/id   nil
                    ::m.categories/name ""}
   ::m.navlinks/id :show-category
   ;; :pre-merge      (u.loader/page-merger model-key {})
   :query          [::m.categories/id
                    ::m.categories/name]}
  (if id
    (ui-container {}
      (ui-segment {}
        (str name)))
    (u.debug/load-error props "admin show category")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["categories"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "admin index categories page"))))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn [_]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["category" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (if (model-key props)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin show category"))
    (u.debug/load-error props "admin show category")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin page of all categories"
   ::m.navlinks/label         "Categories"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin page for category"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Category"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
