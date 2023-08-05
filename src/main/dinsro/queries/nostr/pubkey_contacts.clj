(ns dinsro.queries.nostr.pubkey-contacts
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkey_contacts.clj]]
;; [[../../joins/nostr/pubkey_contacts.cljc]]
;; [[../../model/nostr/pubkey_contacts.cljc]]
;; [[../../ui/nostr/pubkey_contacts.cljs]]

(def model-key ::m.n.pubkey-contacts/id)

(def query-info
  {:ident   model-key
   :pk      '?pubkey-contacts-id
   :clauses [[::m.n.pubkeys/id '?pubkey-actor-id]]
   :rules
   (fn [[pubkey-id] rules]
     (->> rules
          (concat-when pubkey-id
            ['?pubkey-contacts-id ::m.n.pubkey-contacts/actor '?pubkey-actor-id])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.pubkey-contacts/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [:xt/id => (? ::m.n.pubkey-contacts/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.pubkey-contacts/id => nil?]
  (c.xtdb/delete! id))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))

(>defn find-by-actor-and-target
  [actor-id target-id]
  [::m.n.pubkeys/id ::m.n.pubkeys/id => (? ::m.n.pubkey-contacts/id)]
  (log/fine :find-by-actor-and-target/starting {:actor-id actor-id :target-id target-id})
  (c.xtdb/query-value
   '{:find  [?contact-id]
     :in    [[?actor ?target]]
     :where [[?contact-id ::m.n.pubkey-contacts/actor ?actor]
             [?contact-id ::m.n.pubkey-contacts/target ?target]]}
   [actor-id target-id]))
