(ns dinsro.ui.nostr.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.event-tags :as j.n.event-tags]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]))

;; [[../../actions/nostr/event_tags.clj][Event Tag Actions]]
;; [[../../model/nostr/event_tags.cljc][Event Tags Model]]


(def ident-key ::m.n.events/id)
(def router-key :dinsro.ui.nostr.events/Router)

(form/defsc-form AddForm
  [_this _props]
  {fo/id           m.n.event-tags/id
   fo/title        "Tags"
   fo/attributes   [m.n.event-tags/index]
   fo/route-prefix "new-tag"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this AddForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.event-tags/index
                        m.n.event-tags/parent
                        m.n.event-tags/event
                        m.n.event-tags/pubkey]
   ro/controls         {::new           new-button
                        ::refresh       u.links/refresh-control}
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/field-formatters {::m.n.pubkeys/hex #(u.links/ui-pubkey-link %3)}
   ro/source-attribute ::j.n.event-tags/index
   ro/title            "Tags"
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["tags"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))
