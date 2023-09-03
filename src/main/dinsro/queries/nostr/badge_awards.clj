(ns dinsro.queries.nostr.badge-awards
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.options.nostr.badge-awards :as o.n.badge-awards]
   [dinsro.options.nostr.pubkeys :as o.n.pubkeys]
   [dinsro.specs]))

;; [[../../actions/nostr/badge_awards.clj]]
;; [[../../model/nostr/badge_awards.cljc]]
;; [[../../../../notebooks/dinsro/notebooks/nostr/badge_awards_notebook.clj]]

(def model-key o.n.badge-awards/id)

(def query-info
  {:ident   model-key
   :pk      '?badge-awards-id
   :clauses [[o.n.pubkeys/id '?pubkey-id]]
   :rules
   (fn [[pubkey-id] rules]
     (->> rules
          (concat-when pubkey-id
            [['?badge-awards-id o.n.badge-awards/pubkey '?pubkey-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.badge-awards/params => ::m.n.badge-awards/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.badge-awards/id => (? ::m.n.badge-awards/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.badge-awards/id => nil?]
  (c.xtdb/delete! id))
