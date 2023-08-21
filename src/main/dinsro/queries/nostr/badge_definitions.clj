(ns dinsro.queries.nostr.badge-definitions
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]))

;; [[../../actions/nostr/badge_definitions.clj]]
;; [[../../joins/nostr/badge_definitions.cljc]]
;; [[../../model/nostr/badge_definitions.cljc]]
;; [[../../processors/nostr/badge_definitions.clj]]

(def model-key ::m.n.badge-definitions/id)

(def query-info
  {:ident   model-key
   :pk      '?badge-definition-id
   :clauses [[::m.n.pubkeys/id '?pubkey-id]]
   :rules
   (fn [[pubkey-id] rules]
     (->> rules
          (concat-when pubkey-id
            ['?badge-definition-id ::m.n.badge-definitions/pubkey '?pubkey-id])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.badge-definitions/params => ::m.n.badge-definitions/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.badge-definitions/id => (? ::m.n.badge-definitions/item)]
  (c.xtdb/read model-key id))

(>defn delete!
  [id]
  [::m.n.badge-definitions/id => nil?]
  (c.xtdb/delete! id))
