(ns dinsro.ui.settings.categories
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
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]

(def index-page-key :settings-categories)
(def model-key ::m.categories/id)
(def show-page-key :settings-categories-show)

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

(defsc Show
  [_this {::m.categories/keys [id name]
          :as props}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/name ""}
   :pre-merge     (u.loader/page-merger ::m.categories/id {})
   :query         [::m.categories/id
                   ::m.categories/name]}
  (log/debug :Show/starting {:props props})
  (if id
    (dom/div :.ui.container
      (ui-segment {}
        (str name)))
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.categories/name #(u.links/ui-category-link %3)}
   ro/columns           [m.categories/name]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/index
   ro/title             "Categories"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["categories"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (dom/div :.ui.segment "Failed to load report"))))

(defsc ShowPage
  [_this {::m.categories/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.categories/id nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.categories/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["category" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page.")))
