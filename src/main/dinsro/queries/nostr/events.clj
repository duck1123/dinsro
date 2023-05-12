(ns dinsro.queries.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/events.clj][Event Actions]]
;; [[../../model/nostr/events.cljc][Event Model]]
;; [[../../joins/nostr/events.cljc][Event Joins]]
;; [../../ui/nostr/events.cljs]

(def query-info
  {:ident    ::m.n.events/id
   :pk       '?event-id
   :clauses  [[::m.n.pubkeys/id '?pubkey-id]]
   :order-by [['?created-at :desc]]
   :rules
   (fn [[pubkey-id] rules]
     (->> rules
          (concat-when pubkey-id
            [['?event-id ::m.n.events/pubkey '?pubkey-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

;; (defn index-ids
;;   ([] (index-ids {}))
;;   ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  "Create a relay record"
  [params]
  [::m.n.events/params => :xt/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.events/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.events/id => (? ::m.n.events/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.events/id)
      (dissoc record :xt/id))))

(>defn index-ids
  ([]
   [=> (s/coll-of ::m.n.events/id)]
   (index-ids {}))
  ([query-params]
   [any? => (s/coll-of ::m.n.events/id)]
   (do
     (log/info :index-ids/starting {:query-params query-params})
     (let [base-params  (c.xtdb/make-index-query query-info query-params)
           limit-params (c.xtdb/get-limit-options query-params)
           merged-params
           {:order-by [['?created-at :desc]]
            :find     (vec (concat (:find base-params) ['?created-at]))
            :where    (vec (concat
                            (:where base-params)
                            [['?event-id ::m.n.events/created-at '?created-at]]))}
           query        (merge base-params limit-params merged-params)
           index-params (c.xtdb/get-index-params query-info query-params)]
       (log/info :index-ids/query {:query query})
       (let [ids (c.xtdb/query-values query index-params)]
         (log/trace :index-ids/finished {:ids ids})
         ids)))))

(>defn find-by-author
  [pubkey-id]
  [::m.n.pubkeys/id => (s/coll-of ::m.n.events/id)]
  (log/fine :find-by-author/starting {:pubkey-id pubkey-id})
  (c.xtdb/query-values
   '{:find  [?event-id]
     :in    [[?pubkey-id]]
     :where [[?event-id ::m.n.events/pubkey ?pubkey-id]]}
   [pubkey-id]))

(>defn find-by-note-id
  [note-id]
  [::m.n.events/note-id => (? ::m.n.events/id)]
  (log/trace :find-by-note-id/starting {:note-id note-id})
  (c.xtdb/query-value
   '{:find  [?event-id]
     :in    [[?note-id]]
     :where [[?event-id ::m.n.events/note-id ?note-id]]}
   [note-id]))

(>defn register-event!
  [params]
  [::m.n.events/params => ::m.n.events/id]
  (let [note-id (::m.n.events/note-id params)]
    (if-let [event-id (find-by-note-id note-id)]
      event-id
      (do
        (log/info :register-event!/creating {:params params})
        (create-record params)))))

(comment

  (some->
   (index-ids)
   first
   read-record)

  (create-record
   {::m.n.events/addresses "wss://relay.kronkltd.net/"})

  nil)
