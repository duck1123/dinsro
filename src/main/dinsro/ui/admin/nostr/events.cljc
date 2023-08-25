(ns dinsro.ui.admin.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/events.cljc]]
;; [[../../../model/nostr/events.cljc]]
;; [[../../../mutations/nostr/events.cljc]]
;; [[../../../processors/nostr/events.clj]]
;; [[../../../ui/admin/nostr/pubkeys/events.cljc]]
;; [[../../../ui/admin/nostr/relays/events.cljc]]

(def index-page-key :admin-nostr-events)
(def model-key ::m.n.events/id)
(def show-page-key :admin-nostr-events-show)
(def log-props false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.events/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.events/pubkey  #(when %2 (u.links/ui-admin-pubkey-link %2))
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
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.events/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.events/admin-index
   ro/title             "Events"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.events/keys [content pubkey kind sig created-at]
          :as               props}]
  {:ident         ::m.n.events/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key               id
                       ::m.n.events/note-id    ""
                       ::m.n.events/pubkey     {}
                       ::m.n.events/created-at 0
                       ::m.n.events/kind       0
                       ::m.n.events/content    ""
                       ::m.n.events/sig        ""
                       ::m.n.events/deleted?   true}))
   :query         [::m.n.events/id
                   ::m.n.events/note-id
                   {::m.n.events/pubkey (comp/get-query u.links/AdminPubkeyLinkForm)}
                   ::m.n.events/created-at
                   ::m.n.events/kind
                   ::m.n.events/content
                   ::m.n.events/sig
                   ::m.n.events/deleted?]}
  (log/info :Show/starting {:props props})
  (if (model-key props)
    (ui-segment {}
      (dom/div {} (u.links/ui-admin-pubkey-link pubkey))
      (dom/div {} (str content))
      (dom/div {} (str kind))
      (dom/div {} (str sig))
      (dom/div {} (str created-at))
      (when log-props (u.debug/log-props props)))
    (u.debug/load-error props "admin show event")))

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
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["event" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (model-key props)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin show event"))
    (u.debug/load-error props "admin show event page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Events"
   ::m.navlinks/description   "Admin index of events"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin page for an event"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Event"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-nostr
   ::m.navlinks/required-role :admin})
