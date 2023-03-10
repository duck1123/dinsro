(ns dinsro.ui.nostr.pubkeys.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(defsc EventListItem
  [_this {::m.n.events/keys [id content created-at] :as event}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id         nil
                   ::m.n.events/content    ""
                   ::m.n.events/note-id ""
                   ::m.n.events/created-at 0}
   :query         [::m.n.events/id ::m.n.events/content ::m.n.events/created-at ::m.n.events/note-id]}
  (dom/div :.ui.item
    (dom/div :.content
      (dom/div :.ui.segment
        (u.links/ui-event-link event)
        (dom/div {} (str id))
        (dom/div {} (str content))
        (dom/div {} (str created-at))))))

(def ui-event-list-item (comp/factory EventListItem {:keyfn ::m.n.events/id}))

(report/defsc-report Report
  [_this props]
  {ro/BodyItem         EventListItem
   ro/columns          [m.n.events/content]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::refresh        u.links/refresh-control}
   ro/field-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                        ::m.n.events/note-id #(u.links/ui-event-link %3)
                        ::m.n.pubkeys/hex    #(u.links/ui-pubkey-link %3)}
   ro/row-pk           m.n.events/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.pubkeys/events
   ro/title            "Events"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div {:css {:width "500px" :overflow "hidden" :outline "1px solid red"}}
      (dom/div :.ui.items.unstackable
        (map ui-event-list-item current-rows)))))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["events"]}
  ((comp/factory Report) report))
