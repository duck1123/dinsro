(ns dinsro.ui.nostr.relay-pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../model/nostr/relays.cljc][Relay Model]]
;; [[../../model/nostr/relay_pubkeys.cljc][Relay Pubkeys Model]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkeys Mutations]]

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(form/defsc-form AddForm
  [_this _props]
  {fo/id           m.n.pubkeys/id
   fo/title        "Pubkey"
   fo/attributes   [m.n.pubkeys/hex]
   fo/route-prefix "new-pubkey"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this AddForm))})

(defn subscribe-action
  [report-instance {pubkey-id ::m.n.pubkeys/id}]
  (let [relay-id (u.links/get-control-value report-instance ::m.n.relays/id)]
    (log/info :connect-action/starting {:relay-id relay-id :pubkey-id pubkey-id})
    (if relay-id
      (comp/transact!
       report-instance
       [(mu.n.pubkeys/subscribe!
         {::m.n.relays/id  relay-id
          ::m.n.pubkeys/id pubkey-id})])
      (throw (js/Error. "no id")))))

(def subscribe-action-button
  {:label  "Subscribe"
   :action subscribe-action})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/hex
                        m.n.pubkeys/id]
   ro/controls         {::m.n.relays/id {:type :uuid :label "id"}
                        ::new           new-button
                        ::refresh       u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/row-actions [subscribe-action-button]
   ro/source-attribute ::m.n.pubkeys/index
   ro/title            "Pubkeys"
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["pubkeys"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))
