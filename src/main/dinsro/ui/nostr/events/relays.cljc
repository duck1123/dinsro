(ns dinsro.ui.nostr.events.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/relays.cljc]]
;; [[../../../model/nostr/relays.cljc]]

(def index-page-key :nostr-events-show-relays)
(def model-key ::m.n.relays/id)
(def parent-model-key ::m.n.events/id)
(def router-key :dinsro.ui.nostr.events/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.relays/id
                        m.n.relays/address]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {parent-model-key {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.relays/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.relays/index
   ro/title            "Relays"})

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
   :route-segment     ["relays"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "event show relay report"))
    (u.debug/load-error props "event show relay page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Relays"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-events-show
   ::m.navlinks/router        :nostr-events
   ::m.navlinks/required-role :user})
