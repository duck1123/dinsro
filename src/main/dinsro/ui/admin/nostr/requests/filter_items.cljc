(ns dinsro.ui.admin.nostr.requests.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.filter-items :as mu.n.filter-items]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/nostr/filter_items.cljc]]
;; [[../../../../model/nostr/filter_items.cljc]]
;; [[../../../../ui/nostr/filters/filter_items.cljs]]

(def index-page-id :admin-nostr-requests-show-filter-items)
(def model-key ::m.n.filter-items/id)
(def parent-model-key ::m.n.requests/id)
(def parent-router-id :admin-nostr-requests-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.nostr.requests/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.filter-items/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filter-items/id     #(u.links/ui-admin-filter-item-link %2)
                         ::m.n.filter-items/filter #(u.links/ui-admin-filter-link %2)
                         ::m.n.filter-items/pubkey #(u.links/ui-admin-pubkey-link %2)}
   ro/columns           [m.n.filter-items/id
                         m.n.filter-items/filter
                         m.n.filter-items/type
                         m.n.filter-items/kind
                         m.n.filter-items/event
                         m.n.filter-items/pubkey
                         j.n.filter-items/query-string]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/row-actions       [delete-action]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filter-items/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filter-items/admin-index
   ro/title             "Filter Items"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["filter-items"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Items"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/required-role required-role})
