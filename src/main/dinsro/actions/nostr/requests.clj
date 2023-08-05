(ns dinsro.actions.nostr.requests
  (:require
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.filter-items :as q.n.filter-items]
   [dinsro.queries.nostr.filters :as q.n.filters]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/requests.cljc]]
;; [[../../ui/admin/nostr/requests.cljc]]

(defonce request-counter (atom 0))

(defn get-next-code!
  []
  (let [code (str "adhoc " @request-counter)]
    (swap! request-counter inc)
    code))

(>defn create-request
  "Create a request record for a relay with a code"
  [relay-id code]
  [::m.n.relays/id ::m.n.requests/code => ::m.n.requests/id]
  (if-let [request-id (q.n.requests/find-by-relay-and-code relay-id code)]
    (throw (ex-info (str "request already exists - " request-id) {}))
    (let [request-id (q.n.requests/create-record
                      {::m.n.requests/relay relay-id
                       ::m.n.requests/code  code})]
      (log/info :create-request/finished {:request-id request-id})
      request-id)))

(defn register-request
  ([relay-id]
   (register-request relay-id (get-next-code!)))
  ([relay-id code]
   (if-let [request-id (q.n.requests/find-by-relay-and-code relay-id code)]
     request-id
     (create-request relay-id code))))

(s/def ::item-data (s/keys))

(>defn determine-item
  "Create a query map based on item's params"
  [item]
  [::m.n.filter-items/item => (? ::item-data)]
  (or
   (when-let [hex (some-> item ::m.n.filter-items/pubkey
                          q.n.pubkeys/read-record ::m.n.pubkeys/hex)]
     {:pubkey hex})
   (when-let [hex (some-> item ::m.n.filter-items/event
                          q.n.events/read-record ::m.n.events/note-id)]
     {:event hex})
   (when-let [kind (some-> item ::m.n.filter-items/kind)]
     {:kind kind})))

(>defn get-query-string-item
  [item-id]
  [::m.n.filter-items/id => (? ::item-data)]
  (let [item (q.n.filter-items/read-record item-id)
        hex-maps (determine-item item)]
    (log/info :get-query-string/mapped {:item item :hex-maps hex-maps})
    hex-maps))

(>defn get-query-string-filter
  [filter-id]
  [::m.n.filters/id => (s/keys)]
  (log/info :get-query-string-filter/starting {:filter-id filter-id})
  (let [data (->> filter-id
                  q.n.filter-items/find-by-filter
                  (map get-query-string-item)
                  (reduce
                   (fn [val item]
                     (log/info :get-query-string-item/reducing {:val val :item item})
                     (let [{:keys [pubkey event kind]} item]
                       {:ids     (:ids val)
                        :authors (concat (:authors val) (if pubkey [pubkey] []))
                        :kinds   (concat (:kinds val) (if kind [kind] []))
                        :#e      (concat (:#e val) (if event [event] []))
                        :#p      (:#p val)}))
                   {:ids [] :authors [] :kinds [] :#e [] :#p []})
                  (map (fn [[k v]] (when (seq v) [k v])))
                  (filter identity)
                  (into {}))]
    (log/info :get-query-string-filter/finished {:data data})
    data))

(>defn get-query-string
  "Get the query string for the request"
  [request-id]
  [::m.n.requests/id => string?]
  (let [request    (q.n.requests/read-record request-id)
        code       (::m.n.requests/code request)
        filter-ids (q.n.filters/find-by-request request-id)]
    (log/info :get-query-string/found {:filter-ids filter-ids})
    (let [filter-response (mapv get-query-string-filter filter-ids)]
      (json/json-str (concat ["REQ" code] filter-response)))))

(comment

  (q.n.relays/index-ids)

  (def relay-id (q.n.relays/find-by-address "wss://nostr-pub.wellorder.net"))
  relay-id
  (def code "adhoc 0")

  (map q.n.requests/read-record (q.n.requests/find-by-relay relay-id))

  (q.n.requests/find-by-relay relay-id)
  (q.n.requests/find-by-code code)
  (q.n.requests/find-by-relay-and-code relay-id code)

  (ds/gen-key ::m.n.requests/item)

  (def request-id (first (q.n.requests/index-ids)))
  request-id
  (q.n.requests/read-record request-id)

  (q.n.requests/index-ids)

  (get-query-string request-id)

  (q.n.relays/read-record (q.n.requests/find-relay request-id))

  (q.n.requests/delete-all!)

  (q.n.requests/read-record (first (q.n.requests/index-ids)))

  (some-> relay-id q.n.requests/find-relay q.n.relays/read-record)

  ds/date

  (ds/->inst)

  nil)
