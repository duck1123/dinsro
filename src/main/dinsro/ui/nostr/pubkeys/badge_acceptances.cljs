(ns dinsro.ui.nostr.pubkeys.badge-acceptances
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.badge-definitions :as j.n.badge-definitions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def ident-key ::m.n.pubkeys/id)
(def index-page-key :nostr-pubkeys-badge-acceptances)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-acceptances/badge]
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/source-attribute ::j.n.badge-definitions/index
   ro/title            "Badges Acceptances"
   ro/row-pk           m.n.badge-acceptances/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["badge-acceptances"]}
  (ui-report report))
