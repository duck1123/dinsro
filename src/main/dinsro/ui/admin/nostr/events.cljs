(ns dinsro.ui.admin.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/events.cljc]]
;; [[../../../model/nostr/events.cljc]]

(def index-page-key :admin-nostr-events)
(def model-key ::m.n.events/id)
(def show-page-key :admin-nostr-events-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.events/pubkey  #(u.links/ui-admin-pubkey-link %2)
                         ::m.n.events/note-id #(u.links/ui-admin-event-link %3)}
   ro/columns           [m.n.events/note-id
                         m.n.events/pubkey
                         m.n.events/kind
                         m.n.events/content
                         m.n.events/created-at]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         100
   ro/paginate?         true
   ro/row-pk            m.n.events/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.events/index
   ro/title             "Events"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.events/keys [id]
          :as               props}]
  {:ident         ::m.n.events/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key id}))
   :query         [::m.n.events/id]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {} "TODO: Show event")
    (ui-segment {} "Failed to load record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["events"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.n.events/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.events/id     nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.n.events/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["event" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))
