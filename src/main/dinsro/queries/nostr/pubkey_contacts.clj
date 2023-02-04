(ns dinsro.queries.nostr.pubkey-contacts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.pubkey-contacts :as m.n.pubkey-contacts]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/pubkey_contacts.clj][Pubkey Contact Actions]]
;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]


(>defn create-record
  [params]
  [::m.n.pubkey-contacts/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [id     (new-uuid)
        node   (c.xtdb/main-node)
        params (assoc params ::m.n.pubkey-contacts/id id)
        params (assoc params :xt/id id)]
    (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  [id]
  [:xt/id => (? ::m.n.pubkey-contacts/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.pubkey-contacts/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.pubkey-contacts/id)]
  (let [db    (c.xtdb/main-db)
        query '[:find ?e :where [?e ::m.n.pubkey-contacts/id _]]]
    (map first (xt/q db query))))

(>defn delete!
  [id]
  [::m.n.pubkey-contacts/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete! id)))
