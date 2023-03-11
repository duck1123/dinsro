(ns dinsro.queries.nostr.relays
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../nostr.clj][Nostr Queries]]

(>defn create-record
  "Create a relay record"
  [params]
  [::m.n.relays/params => :xt/id]
  (log/info :create-record/starting {:params params})
  (let [connected       (boolean (::m.n.relays/connected params))
        node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.relays/connected connected)
                            (assoc ::m.n.relays/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    (log/info :create-record/finished {:id id})
    id))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.relays/id => (? ::m.n.relays/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.relays/id)
      (dissoc record :xt/id))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.n.relays/id)]
  (log/finer :index-ids/starting {})
  (c.xtdb/query-ids
   '{:find  [?relay-id]
     :where [[?relay-id ::m.n.relays/id _]]}))

(>defn find-by-address
  [address]
  [::m.n.relays/address => (? ::m.n.relays/id)]
  (log/info :find-by-address/starting {:address address})
  (c.xtdb/query-id
   '{:find  [?relay-id]
     :in    [[?address]]
     :where [[?relay-id ::m.n.relays/address ?address]]}
   [address]))

(>defn delete-record
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)] (delete-record id)))

(>defn register-relay
  [address]
  [string? => ::m.n.relays/id]
  (log/info :register-relay/starting {:address address})
  (if-let [id (find-by-address address)]
    id
    (do
      (log/info :register-relay/not-found {})
      (create-record {::m.n.relays/address address}))))

(defn create-connected-toggle
  []
  (log/info :create-connected-toggle/starting {})
  (let [node (c.xtdb/main-node)]
    (xt/await-tx
     node
     (xt/submit-tx
      node
      [[::xt/put
        {:xt/id :toggle-connected
         :xt/fn '(fn [ctx eid connected]
                   (let [;; connected true
                         db           (xtdb.api/db ctx)
                         entity       (xtdb.api/entity db eid)
                         updated-data (assoc entity ::m.n.relays/connected connected)]
                     [[::xt/put updated-data]]))}]]))))

(>defn set-connected
  [relay-id connected]
  [::m.n.relays/id ::m.n.relays/connected => any?]
  (log/info :set-connected/starting {:relay-id relay-id :connected connected})
  (let [node     (c.xtdb/main-node)
        response (xt/submit-tx node [[::xt/fn :toggle-connected relay-id connected]])]
    (log/finer :set-connected/finished {:response response})
    response))

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (create-connected-toggle)
  (log/info :initialize-queries!/finished {}))

(comment

  (some->
   (index-ids)
   first
   read-record)

  (create-record
   {::m.n.relays/addresses "wss://relay.kronkltd.net/"})

  (find-by-address "wss://relay.kronkltd.net/")

  (register-relay "wss://relay.kronkltd.net/")

  (delete-all)

  nil)
