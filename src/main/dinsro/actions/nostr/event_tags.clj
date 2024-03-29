(ns dinsro.actions.nostr.event-tags
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.streams :as a.n.streams]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.queries.nostr.event-tags :as q.n.event-tags]
   [dinsro.queries.nostr.events :as q.n.events]
   [lambdaisland.glogc :as log]))

;; [[../../mutations/nostr/event_tags.cljc]]
;; [[../../processors/nostr/event_tags.clj]]
;; [[../../queries/nostr/event_tags.clj]]
;; [[../../ui/nostr/event_tags.cljs]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/event_tags_notebook.clj]]

(>defn register-tag!
  [event-id tag tag-index]
  [::m.n.events/id any? number? => any?]
  (log/info :register-tag!/start {:event-id event-id :tag tag})
  (let [[type value extra] tag]
    (condp = type
      "p"
      (if-let [pubkey-id (a.n.pubkeys/register-pubkey! value)]
        (q.n.event-tags/create-record
         {::m.n.event-tags/index     tag-index
          ::m.n.event-tags/parent    event-id
          ::m.n.event-tags/type      type
          ::m.n.event-tags/pubkey    pubkey-id
          ::m.n.event-tags/raw-value value
          ::m.n.event-tags/extra     extra})
        (q.n.event-tags/create-record
         {::m.n.event-tags/index     tag-index
          ::m.n.event-tags/parent    event-id
          ::m.n.event-tags/type      type
          ::m.n.event-tags/raw-value value
          ::m.n.event-tags/extra     extra}))
      "e"
      (if-let [target-id (q.n.events/find-by-note-id value)]
        (q.n.event-tags/create-record
         {::m.n.event-tags/index     tag-index
          ::m.n.event-tags/parent    event-id
          ::m.n.event-tags/type      type
          ::m.n.event-tags/raw-value value
          ::m.n.event-tags/event     target-id
          ::m.n.event-tags/extra     extra})
        (q.n.event-tags/create-record
         {::m.n.event-tags/index     tag-index
          ::m.n.event-tags/parent    event-id
          ::m.n.event-tags/type      type
          ::m.n.event-tags/raw-value value
          ::m.n.event-tags/extra     extra}))
      (q.n.event-tags/create-record
       {::m.n.event-tags/index     tag-index
        ::m.n.event-tags/parent    event-id
        ::m.n.event-tags/type      type
        ::m.n.event-tags/raw-value value
        ::m.n.event-tags/extra     extra}))))

(>defn fetch!
  "Fetch the objects associated with this tag"
  [event-tag-id]
  [::m.n.event-tags/id => any?]
  (do
    (log/info :fetch/starting {:event-tag-id event-tag-id})
    (if-let [tag (q.n.event-tags/read-record event-tag-id)]
      (do
        (log/info :fetch!/read {:tag tag})
        (let [{::m.n.event-tags/keys [type]} tag]
          (if (= type "p")
            (let [pubkey-id (::m.n.event-tags/pubkey tag)]
              (log/info :fetch!/pubkey {:pubkey-id pubkey-id
                                        :tag       tag})
              (a.n.streams/enqueue-pubkey-id! pubkey-id))
            (log/info :fetch!/other {})))
        nil)
      nil)))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.event-tags/delete! id))
