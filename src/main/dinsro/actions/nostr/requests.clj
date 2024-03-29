(ns dinsro.actions.nostr.requests
  (:require
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.filters :as a.n.filters]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.queries.nostr.filters :as q.n.filters]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/requests.cljc]]
;; [[../../responses/nostr/requests.cljc]]
;; [[../../ui/admin/nostr/requests.cljc]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/requests_notebook.clj]]

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

(>defn get-query-string
  "Get the query string for the request"
  [request-id]
  [::m.n.requests/id => string?]
  (let [request    (q.n.requests/read-record request-id)
        code       (::m.n.requests/code request)
        filter-ids (q.n.filters/find-by-request request-id)]
    (log/info :get-query-string/found {:filter-ids filter-ids})
    (let [filter-response (mapv a.n.filters/get-query-string filter-ids)]
      (json/json-str (concat ["REQ" code] filter-response)))))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.requests/delete! id))
