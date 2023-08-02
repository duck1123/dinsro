(ns dinsro.queries.nostr.badge-awards
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]
   [xtdb.api :as xt]))

;; [[../../actions/nostr/badge_awards.clj]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/badge_awards_notebook.clj]]

(def query-info
  {:ident   ::m.n.badge-awards/id
   :pk      '?badge-awards-id
   :clauses [[::m.n.pubkeys/id '?pubkey-id]]
   :rules
   (fn [[pubkey-id] rules]
     (->> rules
          (concat-when pubkey-id
            [['?badge-awards-id ::m.n.badge-awards/pubkey '?pubkey-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.badge-awards/params => ::m.n.badge-awards/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.n.badge-awards/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.n.badge-awards/id => (? ::m.n.badge-awards/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.n.badge-awards/id)
      (dissoc record :xt/id))))
