(ns dinsro.ui.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
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


(defn delete-action
  [report-instance {::m.n.events/keys [id]}]
  (form/delete! report-instance ::m.n.events/id id))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(form/defsc-form NewForm [_this _props]
  {fo/id           m.n.events/id
   fo/attributes   [m.n.events/id]
   fo/cancel-route ["events"]
   fo/route-prefix "new-event"
   fo/title        "Event"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Event"
   :action (fn [this _] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.events/note-id
                        m.n.events/pubkey
                        m.n.events/content
                        j.n.events/tag-count
                        m.n.events/created-at]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                        ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/source-attribute ::j.n.events/index
   ro/title            "Events Report"
   ro/row-pk           m.n.events/id
   ro/run-on-mount?    true
   ro/route            "events"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.event-tags/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "tags"
    :name  "Tags"
    :route "dinsro.ui.nostr.event-tags/SubPage"}])

(defsc EventAuthor
  [_this {::m.n.pubkeys/keys [name picture hex nip05]}]
  {:query         [::m.n.pubkeys/id ::m.n.pubkeys/name ::m.n.pubkeys/picture
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/nip05]
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/name    ""
                   ::m.n.pubkeys/picture ""
                   ::m.n.pubkeys/hex     ""
                   ::m.n.pubkeys/nip05   ""}
   :ident         ::m.n.pubkeys/id}
  (dom/div :.ui.segment
    (when picture (dom/img {:src picture :width 100 :height 100}))
    (dom/div :.ui.segment
      (dom/div {} (str name))
      (dom/p {} (str nip05))
      (dom/p {} (str hex)))))

(defsc Show
  [this {::m.n.events/keys [id note-id content pubkey kind sig]
         :ui/keys          [router]
         :as               props}]
  {:route-segment ["event" :id]
   :query         [::m.n.events/id
                   ::m.n.events/note-id
                   ::m.n.events/content
                   {::m.n.events/pubkey (comp/get-query EventAuthor)}
                   ::m.n.events/kind
                   ::m.n.events/sig
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.n.events/id      nil
                   ::m.n.events/note-id ""
                   ::m.n.events/content ""
                   ::m.n.events/pubkey  {}
                   ::m.n.events/kind    nil
                   ::m.n.events/sig     ""
                   :ui/router           {}}
   :ident         ::m.n.events/id
   :pre-merge     (u.links/page-merger ::m.n.events/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.n.events/id ::Show)}
  (dom/div :.ui.segment
    (dom/div :.ui.segment
      (dom/p ((comp/factory EventAuthor) pubkey))
      (dom/p "Content: " content)
      (dom/p "Kind: " (str kind))
      (dom/p "Note Id: " note-id)
      (dom/p "Sig: " (str sig))
      (dom/button
        {:classes [:.ui.button]
         :onClick #(comp/transact! this [(mu.n.events/fetch! {::m.n.events/id id})])}
        "Fetch"))
    (u.links/ui-nav-menu {:menu-items menu-items :id id})
    (eb/error-boundary
     (if router
       (ui-router router)
       (dom/div :.ui.segment
         (dom/h3 {} "Router not loaded")
         (u.links/ui-props-logger props))))))
