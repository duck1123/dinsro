(ns dinsro.ui.nostr.filters.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.filter-items :as o.n.filter-items]
   [dinsro.options.nostr.filters :as o.n.filters]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../actions/nostr/filter_items.clj]]
;; [[../../../model/nostr/filter_items.cljc]]
;; [[../../../queries/nostr/filter_items.clj]]
;; [[../../../ui/nostr/requests/filter_items.cljs]]

(def index-page-id :nostr-filters-show-filter-items)
(def model-key o.n.filter-items/id)
(def parent-model-key o.n.filters/id)
(def parent-router-id :nostr-filters-show)
(def required-role :user)
(def router-key :dinsro.ui.nostr.filters/Router)

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes    [m.n.filter-items/id
                     m.n.filter-items/filter
                     m.n.filter-items/type
                     m.n.filter-items/kind
                     m.n.filter-items/event
                     m.n.filter-items/pubkey]
   fo/cancel-route  ["filter-items"]
   fo/id            m.n.filter-items/id
   fo/route-prefix  "create-filter-item"
   fo/title         "Filter Item"})

(def new-item-button
  {:type   :button
   :local? true
   :label  "New Node"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.n.filter-items/filter #(u.links/ui-filter-link %2)
                         o.n.filter-items/pubkey #(and %2 (u.links/ui-pubkey-link %2))}
   ro/columns           [m.n.filter-items/filter
                         m.n.filter-items/index
                         m.n.filter-items/kind
                         m.n.filter-items/type
                         m.n.filter-items/event
                         m.n.filter-items/pubkey]
   ro/control-layout    {:action-buttons [::add ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::add            new-item-button
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filter-items/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filter-items/index
   ro/title             "Filter Items"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["items"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Items"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
