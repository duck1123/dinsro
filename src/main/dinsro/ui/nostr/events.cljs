(ns dinsro.ui.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.events.event-tags :as u.n.e.event-tags]
   [dinsro.ui.nostr.events.relays :as u.n.e.relays]
   [dinsro.ui.nostr.events.witnesses :as u.n.e.witnesses]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as transform]
   [sablono.core :as html :refer-macros [html]]))

;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../joins/nostr/events.cljc][Event Joins]]
;; [[../../mutations/nostr/events.cljc][Event Mutations]]

(def log-event-props false)

(def menu-items
  [{:key   "tags"
    :name  "Tags"
    :route "dinsro.ui.nostr.events.event-tags/SubPage"}
   {:key   "witnesses"
    :name  "Witnesses"
    :route "dinsro.ui.nostr.events.witnesses/SubPage"}
   {:key   "relays"
    :name  "Relays"
    :route "dinsro.ui.nostr.events.relays/SubPage"}])

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.n.events/id]
   fo/cancel-route ["events"]
   fo/id           m.n.events/id
   fo/route-prefix "new-event"
   fo/title        "Event"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Event"
   :action (fn [this _] (form/create! this NewForm))})

(defsc EventAuthorImage
  [_this {::m.n.pubkeys/keys [picture]}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/name    ""
                   ::m.n.pubkeys/picture ""}
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/picture]}
  (when picture (dom/img {:src picture :width 100 :height 100})))

(defsc EventAuthor
  [_this {::m.n.pubkeys/keys [picture]}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/picture ""
                   ::m.n.pubkeys/hex     ""
                   ::m.n.pubkeys/nip05   ""}
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/picture
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/nip05]}
  (when picture (dom/img {:src picture :width 100 :height 100})))

(def ui-event-author-image (comp/factory EventAuthorImage))

(def transform-markup true)
(def convert-html true)
(def show-ast false)

(defsc TagDisplay
  [_this {::m.n.event-tags/keys [pubkey event index raw-value extra type]}]
  {:query         [::m.n.event-tags/id
                   {::m.n.event-tags/pubkey (comp/get-query u.links/PubkeyNameLinkForm)}
                   {::m.n.event-tags/event (comp/get-query u.links/ui-event-link)}
                   ::m.n.event-tags/index
                   ::m.n.event-tags/raw-value
                   ::m.n.event-tags/extra
                   ::m.n.event-tags/type]
   :ident         ::m.n.event-tags/id
   :initial-state {::m.n.event-tags/id        nil
                   ::m.n.event-tags/pubkey    {}
                   ::m.n.event-tags/event     {}
                   ::m.n.event-tags/index     0
                   ::m.n.event-tags/raw-value nil
                   ::m.n.event-tags/extra     nil
                   ::m.n.event-tags/type      nil}}
  (let [show-labels false]
    (dom/div :.ui.item
      (dom/div {} "[" (str index) "] ")
      (when pubkey
        (dom/div {}
          (when show-labels "Pubkey: ")
          (u.links/ui-pubkey-name-link pubkey)))
      (when event
        (dom/div {}
          (when show-labels "Event: ")
          (u.links/ui-event-link event)))
      (when-not (or pubkey event)
        (comp/fragment
         (dom/div {} "Type: " (str type))
         (dom/div {} "Raw Value: " (str raw-value))))
      (when extra (dom/div {} "Extra: " (str extra))))))

(def ui-tag-display (comp/factory TagDisplay {:keyfn ::m.n.event-tags/id}))

(def log-run-props false)
(def log-witness-props false)
(def log-connection-props true)

(defsc ConnectionDisplay
  [_this {::m.n.connections/keys [relay]}]
  {:ident         ::m.n.connections/id
   :initial-state {::m.n.connections/id    nil
                   ::m.n.connections/relay {}}
   :query         [::m.n.connections/id
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}]}
  (u.links/ui-relay-link relay))

(def ui-connection-display (comp/factory ConnectionDisplay {:keyfn ::m.n.connections/id}))

(defsc RunDisplay
  [_this {::m.n.runs/keys [connection]}]
  {:ident         ::m.n.runs/id
   :initial-state {::m.n.runs/id         nil
                   ::m.n.runs/connection {}}
   :query         [::m.n.runs/id
                   {::m.n.runs/connection (comp/get-query ConnectionDisplay)}]}
  (ui-connection-display connection))

(def ui-run-display (comp/factory RunDisplay {:keyfn ::m.n.runs/id}))

(defsc WitnessDisplay
  [_this {::m.n.witnesses/keys [run]}]
  {:ident         ::m.n.witnesses/id
   :query         [::m.n.witnesses/id
                   {::m.n.witnesses/run (comp/get-query RunDisplay)}]
   :initial-state {::m.n.witnesses/id  nil
                   ::m.n.witnesses/run {}}}
  (dom/div :.ui.item (ui-run-display run)))

(def ui-witness-display (comp/factory WitnessDisplay {:keyfn ::m.n.witnesses/id}))

(defsc EventBox
  [_this {::m.n.events/keys [content pubkey note-id]
          ::j.n.events/keys [created-date tags witnesses]
          :as               props}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id           nil
                   ::m.n.events/pubkey       {}
                   ::m.n.events/note-id      ""
                   ::m.n.events/content      ""
                   ::m.n.events/created-at   0
                   ::j.n.events/created-date nil
                   ::j.n.events/witnesses    []
                   ::j.n.events/tags         []}
   :query         [::m.n.events/id
                   ::m.n.events/content
                   ::m.n.events/note-id
                   ::m.n.events/created-at
                   ::j.n.events/created-date
                   {::j.n.events/witnesses (comp/get-query WitnessDisplay)}
                   {::m.n.events/pubkey (comp/get-query EventAuthor)}
                   {::j.n.events/tags (comp/get-query TagDisplay)}]}
  (dom/div :.item.segment
    (dom/div :.ui.tiny.image
      (ui-event-author-image pubkey))
    (dom/div :.content
      (dom/div {:classes [:.header]}
        (u.links/ui-pubkey-name-link pubkey))
      (dom/div {:classes [:.meta]}
        (dom/span {:classes [:.date]}
                  (u.links/ui-event-created-link props)
                  (str created-date))
        (dom/div {} (str note-id)))
      (dom/div {:classes [:.description]}
        (let [ast (md/parse content)]
          (if show-ast
            (u.links/log-props ast)
            (if transform-markup
              (let [hiccup (transform/->hiccup ast)]
                (if convert-html
                  (html hiccup)
                  (str hiccup)))
              (str content)))))
      (dom/div :.extra
        (when log-event-props (u.links/log-props props))
        (dom/div :.ui.items
          (map ui-witness-display witnesses))
        (dom/div :.ui.items
          (map ui-tag-display  (sort-by ::m.n.event-tags/index tags)))))))

(def ui-event-box (comp/factory EventBox {:keyfn ::m.n.events/id}))

(def override-report false)

(report/defsc-report Report
  [this props]
  {ro/BodyItem          EventBox
   ro/column-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                         ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/columns           [m.n.events/content]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/route             "events"
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.events/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.events/index
   ro/title             "Events Report"}
  (if override-report
    (report/render-layout this)
    (let [{:ui/keys [current-rows]} props]
      (dom/div {:classes [:.ui :.segment]}
        (dom/div {:classes [:.ui :.container]}
          ((report/control-renderer this) this)
          (dom/div {:classes [:.ui :.items :.unstackable]}
            (map ui-event-box current-rows)))))))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.e.event-tags/SubPage
    u.n.e.relays/SubPage
    u.n.e.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [this {::m.n.events/keys [id content pubkey kind sig created-at]
         :ui/keys          [router]}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id         nil
                   ::m.n.events/note-id    ""
                   ::m.n.events/content    ""
                   ::m.n.events/pubkey     {}
                   ::m.n.events/kind       nil
                   ::m.n.events/created-at 0
                   ::m.n.events/sig        ""
                   :ui/router              {}}
   :pre-merge     (u.links/page-merger ::m.n.events/id {:ui/router Router})
   :query         [::m.n.events/id
                   ::m.n.events/content
                   {::m.n.events/pubkey (comp/get-query EventAuthorImage)}
                   ::m.n.events/kind
                   ::m.n.events/note-id
                   ::m.n.events/created-at
                   ::m.n.events/sig
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["event" :id]
   :will-enter    (partial u.links/page-loader ::m.n.events/id ::Show)}
  (dom/div :.ui.segment
    (dom/div :.ui.segment
      (dom/div :.ui.items.unstackable
        (dom/div :.item
          (dom/div :.ui.tiny.image
            (ui-event-author-image pubkey))
          (dom/div :.content
            (dom/div {:classes [:.header]}
              (u.links/ui-pubkey-name-link pubkey))
            (dom/div {:classes [:.meta]}
              (dom/span {:classes [:.date]}
                        (str created-at) " - " (str kind)))
            (dom/div {:classes [:.description]}
              (str content))
            (dom/div {} "Sig: " (str sig))
            (dom/div :.actions
              (dom/a
                {:classes [:.ui.reply]
                 :onClick #(comp/transact! this [(mu.n.events/fetch! {::m.n.events/id id})])}
                "Fetch"))))))
    (u.links/ui-nav-menu {:menu-items menu-items :id id})
    ((comp/factory Router) router)))
