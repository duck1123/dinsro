(ns dinsro.ui.admin.nostr.pubkeys.badge-acceptances
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
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :admin-nostr-pubkeys-show-badge-acceptances)
(def model-key ::m.n.badge-acceptances/id)
(def parent-model-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.admin.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-acceptances/badge]
   ro/controls         {parent-model-key {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/source-attribute ::j.n.badge-definitions/admin-index
   ro/title            "Badges Acceptances"
   ro/row-pk           m.n.badge-acceptances/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["badge-acceptances"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Badge Acceptances"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr-pubkeys-show
   ::m.navlinks/router        :admin-nostr-pubkeys
   ::m.navlinks/required-role :admin})
