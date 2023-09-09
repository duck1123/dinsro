(ns dinsro.ui.settings.categories
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
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.options.categories :as o.categories]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.settings.categories :as u.f.s.categories]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/categories.cljc]]
;; [[../../model/categories.cljc]]
;; [[../../mutations/categories.cljc]]
;; [[../../options/categories.cljc]]

(def index-page-id :settings-categories)
(def model-key o.categories/id)
(def parent-router-id :settings)
(def required-role :user)
(def show-page-id :settings-categories-show)

(def debug-props? true)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.categories/delete!))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.s.categories/NewForm))})

(defsc Show
  [_this {::m.categories/keys [id name]
          :as                 props}]
  {:ident         ::m.categories/id
   :initial-state (fn [props]
                    {o.categories/id   (model-key props)
                     o.categories/name ""})
   :pre-merge     (u.loader/page-merger o.categories/id {})
   :query         (fn []
                    [o.categories/id
                     o.categories/name])}
  (log/debug :Show/starting {:props props})
  (if id
    (ui-container {}
      (ui-segment {}
        (dom/div {} (str name)))
      (when debug-props?
        (u.debug/log-props props)))
    (u.debug/load-error props "settings show category")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.categories/name #(u.links/ui-category-link %3)}
   ro/columns           [m.categories/name]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/index
   ro/title             "Categories"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["categories"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "settings categories"))))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state (fn [_props]
                    {o.categories/id nil
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
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
