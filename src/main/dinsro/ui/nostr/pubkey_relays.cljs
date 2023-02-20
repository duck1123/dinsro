(ns dinsro.ui.nostr.pubkey-relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.links :as u.links]))

;; [[../../model/nostr/relays.cljc][Relays Model]]

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.relays/address j.n.relays/subscription-count]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/field-formatters {::m.n.relays/address #(u.links/ui-relay-link %3)}
   ro/row-actions      [(u.links/subrow-action-button "Fetch" ::m.n.relays/id ident-key mu.n.relays/fetch!)
                        (u.links/subrow-action-button "Delete" ::m.n.relays/id ident-key mu.n.relays/delete!)]
   ro/row-pk           m.n.relays/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.relays/index
   ro/title            "Relays"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :route-segment     ["relays"]}
  ((comp/factory Report) report))
