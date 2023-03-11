(ns dinsro.ui.nostr.relays.topics
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(form/defsc-form AddForm
  [_this _props]
  {fo/id           m.n.pubkeys/id
   fo/title        "Topic"
   fo/attributes   [m.n.pubkeys/hex]
   fo/route-prefix "new-topic"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this AddForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/picture
                        m.n.pubkeys/name
                        j.n.pubkeys/subscription-count]

   ro/controls         {::m.n.relays/id {:type :uuid :label "id"}
                        ::new           new-button
                        ::refresh       u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/field-formatters {::m.n.pubkeys/name #(u.links/ui-pubkey-name-link %3)}
   ro/source-attribute ::j.n.pubkeys/index
   ro/title            "Topics"
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["topics"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
