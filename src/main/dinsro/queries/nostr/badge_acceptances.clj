(ns dinsro.queries.nostr.badge-acceptances
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs]))

;; [[../../model/nostr/badge_acceptances.cljc]]

(def model-key ::m.n.badge-acceptances/id)

(def query-info
  {:ident   model-key
   :pk      '?badge-acceptance-id
   :clauses [[::m.n.badge-definitions/id '?badge-definition-id]
             [::m.n.pubkeys/id           '?pubkey-id]]
   :rules
   (fn [[badge-definition-id pubkey-id] rules]
     (->> rules
          (concat-when badge-definition-id
            [['?badge-acceptance-id ::m.n.badge-acceptances/badge  '?badge-definition-id]])
          (concat-when pubkey-id
            [['?badge-acceptance-id ::m.n.badge-acceptances/pubkey '?pubkey-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  [params]
  [::m.n.badge-acceptances/params => ::m.n.badge-acceptances/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.n.badge-acceptances/id => (? ::m.n.badge-acceptances/item)]
  (c.xtdb/read model-key id))
