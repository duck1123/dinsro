(ns dinsro.ui.nostr.relays.pubkeys
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/pubkeys.cljc][Pubkeys Join]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../model/nostr/relays.cljc][Relay Model]]
;; [[../../model/nostr/relay_pubkeys.cljc][Relay Pubkeys Model]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkeys Mutations]]
;; [[../../ui/nostr/relays.cljs][Relays UI]]

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

(defn fetch-action
  [report-instance {pubkey-id ::m.n.pubkeys/id
                    :as props}]
  (log/info :fetch-action/starting {:report-instance report-instance
                                    :props props
                                    :p (comp/props report-instance)})
  (let [relay-id (u.links/get-control-value report-instance ::m.n.relays/id)]
    (log/info :subscribe-action/starting {:relay-id relay-id :pubkey-id pubkey-id})
    (if relay-id
      (let [props {::m.n.relays/id relay-id ::m.n.pubkeys/id pubkey-id}]
        (comp/transact! report-instance [(mu.n.pubkeys/fetch! props)]))
      (throw (js/Error. "no id")))))

(defn fetch-events-action
  [report-instance {pubkey-id ::m.n.pubkeys/id
                    :as       props}]
  (log/info :fetch-action/starting {:report-instance report-instance
                                    :props           props})
  (let [relay-id (u.links/get-control-value report-instance ::m.n.relays/id)
        props {::m.n.relays/id relay-id ::m.n.pubkeys/id pubkey-id}]
    (comp/transact! report-instance [(mu.n.events/fetch-events! props)])))

(defn subscribe-action
  [report-instance {pubkey-id ::m.n.pubkeys/id}]
  (let [relay-id (u.links/get-control-value report-instance ::m.n.relays/id)]
    (log/info :subscribe-action/starting {:relay-id relay-id :pubkey-id pubkey-id})
    (if relay-id
      (let [props {::m.n.relays/id relay-id ::m.n.pubkeys/id pubkey-id}]
        (comp/transact! report-instance [(mu.n.pubkeys/subscribe! props)]))
      (throw (js/Error. "no id")))))

(def fetch-action-button
  {:label  "Fetch"
   :action fetch-action})

(def fetch-events-button
  {:label  "Fetch Events"
   :action fetch-events-action})

(def subscribe-action-button
  {:label  "Subscribe"
   :action subscribe-action})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/picture
                        m.n.pubkeys/name
                        j.n.pubkeys/subscription-count]
   ro/controls         {::m.n.relays/id {:type :uuid :label "id"}
                        ::new           new-button
                        ::refresh       u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/field-formatters {::m.n.pubkeys/name #(u.links/ui-pubkey-name-link %3)
                        ::m.n.pubkeys/picture
                        (fn [_ picture] (if picture
                                          (dom/img {:src picture :width 100 :height 100})
                                          ""))}
   ro/row-actions      [fetch-action-button
                        subscribe-action-button
                        fetch-events-button]
   ro/source-attribute ::j.n.pubkeys/index
   ro/title            "Pubkeys"
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["pubkeys"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
