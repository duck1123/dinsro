(ns dinsro.ui.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]
;; [[../ui/admin/users/categories.cljs]]

(def index-page-key :categories)
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
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/index
   ro/title             "Categories"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.categories/keys [name]}]
  {:ident          ::m.categories/id
   :initial-state  {::m.categories/id   nil
                    ::m.categories/name ""}
   ::m.navlinks/id :show-category
   :pre-merge      (u.loader/page-merger ::m.categories/id {})
   :query          [::m.categories/id
                    ::m.categories/name]}
  (dom/div :.ui.container
    (dom/div :.ui.segment
      (str name))))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this {::m.categories/keys [id]
          :ui/keys            [record]}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_props]
                    {::m.categories/id nil
                     ::m.navlinks/id   show-page-key
                     :ui/record        (comp/get-initial-state Show)})
   :query         (fn [_props] [model-key ::m.navlinks/id {:ui/record (comp/get-query Show)}])
   :route-segment ["category" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::Show)}
  (if (and record id)
    (ui-show record)
    (ui-segment {} "Failed to load record")))

(defsc IndexPage
  [_this _props]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["categories"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {} "Index Categories"))
