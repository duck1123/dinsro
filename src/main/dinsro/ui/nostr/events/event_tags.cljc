(ns dinsro.ui.nostr.events.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.event-tags :as j.n.event-tags]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.event-tags :as o.n.event-tags]
   [dinsro.options.nostr.events :as o.n.events]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.forms.nostr.events.event-tags :as u.f.n.e.event-tags]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../actions/nostr/event_tags.clj]]
;; [[../../model/nostr/event_tags.cljc]]

(def index-page-id :nostr-events-show-event-tags)
(def model-key o.n.event-tags/id)
(def parent-model-key o.n.events/id)
(def parent-router-id :nostr-events-show)
(def required-role :user)
(def router-key :dinsro.ui.nostr.events/Router)

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.n.e.event-tags/AddForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.n.event-tags/event  #(u.links/ui-event-link %2)
                         o.n.event-tags/pubkey #(u.links/ui-pubkey-link %2)}
   ro/columns           [m.n.event-tags/index
                         m.n.event-tags/event
                         m.n.event-tags/pubkey]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::new            new-button
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.event-tags/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.event-tags/index
   ro/title             "Tags"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         o.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn [_]
                        [[::dr/id router-key]
                         parent-model-key
                         o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["tags"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Tags"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
