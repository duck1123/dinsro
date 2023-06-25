(ns dinsro.ui.nostr.events.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.event-tags :as j.n.event-tags]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../actions/nostr/event_tags.clj][Event Tag Actions]]
;; [[../../model/nostr/event_tags.cljc][Event Tags Model]]

(def ident-key ::m.n.events/id)
(def index-page-key :nostr-events-event-tags)
(def router-key :dinsro.ui.nostr.events/Router)

(form/defsc-form AddForm
  [_this _props]
  {fo/attributes   [m.n.event-tags/index]
   fo/id           m.n.event-tags/id
   fo/route-prefix "new-tag"
   fo/title        "Tags"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this AddForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.event-tags/event  #(u.links/ui-event-link %2)
                         ::m.n.event-tags/pubkey #(u.links/ui-pubkey-link %2)}
   ro/columns           [m.n.event-tags/index
                         m.n.event-tags/event
                         m.n.event-tags/pubkey]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.event-tags/index
   ro/title             "Tags"})

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
   :route-segment     ["tags"]}
  (ui-report report))
