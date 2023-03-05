(ns dinsro.ui.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.events :as mu.n.events]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]))

;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../joins/nostr/events.cljc][Event Joins]]
;; [[../../mutations/nostr/events.cljc][Event Mutations]]

(def menu-items
  [{:key   "tags"
    :name  "Tags"
    :route "dinsro.ui.nostr.event-tags/SubPage"}])

(defn delete-action
  [report-instance {::m.n.events/keys [id]}]
  (form/delete! report-instance ::m.n.events/id id))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

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
                   ::m.n.pubkeys/name    ""
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

(defsc EventBox
  [_this {::m.n.events/keys [content created-at pubkey]}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id         nil
                   ::m.n.events/pubkey     {}
                   ::m.n.events/content    ""
                   ::m.n.events/created-at 0}
   :query         [::m.n.events/id ::m.n.events/content ::m.n.events/created-at
                   {::m.n.events/pubkey (comp/get-query EventAuthor)}]}
  (dom/div :.item
    (dom/div :.ui.tiny.image
      (ui-event-author-image pubkey))
    (dom/div :.content
      (dom/a {:classes [:.header]} (u.links/ui-pubkey-name-link pubkey))
      (dom/div {:classes [:.meta]}
        (dom/span {:classes [:.date]} (str created-at)))
      (dom/div {:classes [:.description]}
        (str content))
      (dom/div {:classes [:.actions]}
        (dom/a {:classes [:.reply]} "Reply")))))

(def ui-event-box (comp/factory EventBox))

(report/defsc-report Report
  [_this props]
  {ro/BodyItem         EventBox
   ro/columns          [m.n.events/content]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                        ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/route            "events"
   ro/row-pk           m.n.events/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.events/index
   ro/title            "Events Report"}
  (let [{:ui/keys [current-rows]} props]
    (dom/div {:classes [:.ui :.segment]}
      (dom/div {:classes [:.ui :.container]}
        (dom/div {:classes [:.ui :.items]}
          (map ui-event-box current-rows))))))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.event-tags/SubPage]})

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
                   ::m.n.events/note-id
                   ::m.n.events/content
                   {::m.n.events/pubkey (comp/get-query EventAuthorImage)}
                   ::m.n.events/kind
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
