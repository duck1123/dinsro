(ns dinsro.queries.nostr.pubkey-contacts
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/pubkey_contacts.clj][Pubkey Contact Actions]]
;; [[../../joins/nostr/pubkey_contacts.cljc][Pubkey Contact Joins]]
;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]
;; [[../../ui/nostr/pubkey_contacts.cljs][Pubkey Contacts UI]]

(def query-info
  {:ident   ::m.n.pubkey-contacts/id
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
  (let [id     (new-uuid)
        node   (c.xtdb/get-node)
        params (assoc params ::m.n.pubkey-contacts/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.n.pubkey-contacts/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.pubkey-contacts/id)
      (dissoc record :xt/id))))

(>defn delete!
  [id]
  [::m.n.pubkey-contacts/id => nil?]
  (let [node (c.xtdb/get-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

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
