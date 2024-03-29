(ns dinsro.ui.admin.nostr.requests.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.filters :as mu.n.filters]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/nostr/filters.cljc]]
;; [[../../../../model/nostr/filters.cljc]]
;; [[../../../../ui/admin/nostr/filters.cljc]]
;; [[../../../../ui/admin/nostr/requests.cljc]]

(def index-page-id :admin-nostr-requests-show-filters)
(def model-key ::m.n.filters/id)
(def parent-model-key ::m.n.requests/id)
(def parent-router-id :admin-nostr-requests-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.nostr.requests/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.filters/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filters/index      #(u.links/ui-admin-filter-link %3)
                         ::m.n.filters/request    #(u.links/ui-admin-request-link %2)
                         ::j.n.filters/item-count #(u.links/ui-admin-filter-item-count-link %3)}
   ro/columns           [m.n.filters/index
                         m.n.filters/request
                         j.n.filters/item-count
                         j.n.filters/query-string]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::add-filter     (u.buttons/sub-page-action-button
                                           {:label      "Add Filter"
                                            :mutation   mu.n.filters/add-filter!
                                            :parent-key parent-model-key})
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.filters/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filters/admin-index
   ro/title             "Filters"})

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
   :route-segment     ["filters"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Filters"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
