(ns dinsro.ui.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.categories :as o.categories]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.forms.categories :as u.f.categories]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../joins/categories.cljc]]
;; [[../model/categories.cljc]]
;; [[../ui/admin/users/categories.cljs]]

(def index-page-id :categories)
(def model-key o.categories/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :categories-show)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.categories/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.categories/user #(u.links/ui-user-link %2)}
   ro/columns           [m.categories/name
                         m.categories/user]
   ro/control-layout    {:action-buttons [::new]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/form-links        {o.categories/name u.f.categories/CategoryForm}
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
   :initial-state  (fn [_props]
                     {o.categories/id   nil
                      o.categories/name ""})
   ::m.navlinks/id :show-category
   :pre-merge      (u.loader/page-merger model-key {})
   :query          (fn []
                     [o.categories/id
                      o.categories/name])}
  (ui-container {}
    (ui-segment {}
      (str name))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this _props]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["categories"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {} "Index Categories"))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show)})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["category" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Categories"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Category"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
