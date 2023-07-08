(ns dinsro.ui.admin.nostr.requests.filter-items
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.filter-items :as mu.n.filter-items]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../filters/filter_items.cljs]]
;; [[../../../../joins/nostr/filter_items.cljc]]
;; [[../../../../model/nostr/filter_items.cljc]]

(def index-page-key :admin-nostr-requests-show-filter-items)
(def model-key ::m.n.filter-items/id)
(def parent-model-key ::m.n.requests/id)
(def router-key :dinsro.ui.nostr.requests/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filter-items/filter #(u.links/ui-filter-link %2)
                         ::m.n.filter-items/pubkey #(u.links/ui-pubkey-link %2)}
   ro/columns           [m.n.filter-items/id
                         m.n.filter-items/filter
                         m.n.filter-items/type
                         m.n.filter-items/kind
                         m.n.filter-items/event
                         m.n.filter-items/pubkey]
   ro/control-layout    {:action-buttons [::add-filter ::new ::refresh]}
   ro/controls          {::m.n.requests/id {:type :uuid :label "id"}
                         ::refresh         u.links/refresh-control}
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.n.filter-items/delete!)]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filter-items/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filter-items/index
   ro/title             "Filter Items"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.n.requests/keys [id]
          :ui/keys            [report]
          :as                 props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id   index-page-key
                       ::m.n.requests/id nil
                       :ui/report        {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.n.requests/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["filter-items"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
