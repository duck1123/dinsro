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
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../actions/nostr/filter_items.clj]]
;; [[../../../model/nostr/filter_items.cljc]]
;; [[../../../queries/nostr/filter_items.clj]]
;; [[../../../ui/nostr/requests/filter_items.cljs]]

(def index-page-key :nostr-filters-show-filter-items)
(def model-key ::m.n.filter-items/id)
(def parent-model-key ::m.n.filters/id)
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
  {ro/column-formatters {::m.n.filter-items/filter #(u.links/ui-filter-link %2)
                         ::m.n.filter-items/pubkey #(and %2 (u.links/ui-pubkey-link %2))}
   ro/columns           [m.n.filter-items/filter
                         m.n.filter-items/index
                         m.n.filter-items/kind
                         m.n.filter-items/type
                         m.n.filter-items/event
                         m.n.filter-items/pubkey]
   ro/control-layout    {:action-buttons [::add ::refresh]}
   ro/controls          {::m.n.filters/id {:type :uuid :label "id"}
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
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["items"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute   index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Items"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-filters-show
   ::m.navlinks/router        :nostr-filters
   ::m.navlinks/required-role :user})
