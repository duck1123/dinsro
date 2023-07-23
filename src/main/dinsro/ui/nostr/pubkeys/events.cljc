(ns dinsro.ui.nostr.pubkeys.events
  "Events in the context of a pubkey"
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.nostr.events :as u.n.events]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as transform]))

;; [[../../../model/nostr/events.cljc]]
;; [[../../../ui/nostr/event_tags.cljs]]

(def index-page-key :nostr-pubkeys-show-events)
(def model-key ::m.n.events/id)
(def parent-model-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(defsc EventListItem
  "A single event in a list of events"
  [_this {::m.n.events/keys [id content created-at] :as event}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id         nil
                   ::m.n.events/content    ""
                   ::m.n.events/note-id ""
                   ::m.n.events/created-at 0}
   :query         [::m.n.events/id ::m.n.events/content ::m.n.events/created-at ::m.n.events/note-id]}
  (dom/div :.ui.item
    (dom/div :.content
      (ui-segment {}
        (u.links/ui-event-link event)
        (dom/div {} (str id))
        (dom/div {} (str (transform/->hiccup (md/parse content))))
        (dom/div {} (str created-at))))))

(def ui-event-list-item (comp/factory EventListItem {:keyfn ::m.n.events/id}))

(def override-report false)

;; "Events in the context of a pubkey"
(report/defsc-report Report
  [this props]
  {ro/BodyItem            u.n.events/EventBox
   ro/column-formatters   {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                           ::m.n.events/note-id #(u.links/ui-event-link %3)
                           ::m.n.pubkeys/hex    #(u.links/ui-pubkey-link %3)}
   ro/columns             [m.n.events/content]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::m.n.pubkeys/id {:type :uuid :label "id"}
                           ::refresh        u.links/refresh-control}
   ro/initial-sort-params {:sort-by    ::m.n.events/created-at
                           :ascending? false}
   ro/machine             spr/machine
   ro/page-size           10
   ro/paginate?           true
   ro/row-pk              m.n.events/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.n.events/index
   ro/title               "Events"}
  (if override-report
    (report/render-layout this)
    (let [{:ui/keys [current-rows]} props]
      (ui-segment {}
        (dom/div {:classes [:.ui :.container]}
          ((report/control-renderer this) this)
          (dom/div {:classes [:.ui :.items :.unstackable]}
            (map u.n.events/ui-event-box current-rows)))))))

(def ui-report (comp/factory Report))

(defsc SubPage
  "Event subpage for events"
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["events"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Events"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-pubkeys-show
   ::m.navlinks/router        :nostr-pubkeys
   ::m.navlinks/required-role :user})
