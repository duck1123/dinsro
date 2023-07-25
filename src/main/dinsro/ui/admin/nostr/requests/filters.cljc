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
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/nostr/filters.cljc]]
;; [[../../../../model/nostr/filters.cljc]]

(def index-page-key :admin-nostr-requests-show-filters)
(def model-key ::m.n.filters/id)
(def parent-model-key ::m.n.requests/id)
(def router-key :dinsro.ui.nostr.requests/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filters/index      #(u.links/ui-filter-link %3)
                         ::m.n.filters/request    #(u.links/ui-request-link %2)
                         ::j.n.filters/item-count #(u.links/ui-filter-item-count-link %3)}
   ro/columns           [m.n.filters/index
                         m.n.filters/request
                         j.n.filters/item-count]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {::m.n.requests/id {:type :uuid :label "id"}
                         ::add-filter      (u.buttons/sub-page-action-button
                                            {:label      "Add Filter"
                                             :mutation   mu.n.filters/add-filter!
                                             :parent-key parent-model-key})
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.n.filters/delete!)]
   ro/row-pk            m.n.filters/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filters/index
   ro/title             "Filters"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [_]
                        {::m.navlinks/id  index-page-key
                         parent-model-key nil
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn [_]
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["filters"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (get props parent-model-key)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin request filters report"))
    (u.debug/load-error props "admin request filters")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Filters"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr-requests-show
   ::m.navlinks/router        :admin-nostr-requests
   ::m.navlinks/required-role :admin})
