(ns dinsro.ui.admin.nostr.relays.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/nostr/witnesses.cljc]]
;; [[../../../../model/nostr/witnesses.cljc]]

(def index-page-key :admin-nostr-relays-show-witnesses)
(def model-key ::m.n.witnesses/id)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.witnesses/id]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::m.n.relays/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.witnesses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.witnesses/index
   ro/title            "Witnesses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.n.relays/keys [id]
          :ui/keys          [report]
          :as               props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.n.relays/id nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.n.relays/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["witnesses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))
