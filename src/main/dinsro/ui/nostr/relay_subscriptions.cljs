(ns dinsro.ui.nostr.relay-subscriptions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.ui.links :as u.links]))

;; [[../../model/nostr/relays.cljc][Relay Model]]
;; [[../../model/nostr/relay_subscriptions.cljc][Relay Subscriptions Model]]

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.subscriptions/code]
   ro/controls         {::m.c.nodes/id {:type :uuid :label "id"}
                        ::refresh      u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.n.subscriptions/index
   ro/title            "Relays"
   ro/row-pk           m.n.subscriptions/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["relays"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))
