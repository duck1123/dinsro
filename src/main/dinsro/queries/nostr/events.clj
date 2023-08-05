(ns dinsro.queries.nostr.events
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/events.clj]]
;; [[../../model/nostr/events.cljc]]
;; [[../../joins/nostr/events.cljc]]
;; [[../../ui/nostr/events.cljs]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/events_notebook.clj]]

(def model-key ::m.n.events/id)

(def query-info
  {:ident    model-key
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
  (c.xtdb/create! model-key params))

(>defn read-record
  "Read a relay record"
  [id]
  [::m.n.events/id => (? ::m.n.events/item)]
  (c.xtdb/read model-key id))

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
