(ns dinsro.queries.core.script-sigs
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.core.connections :as m.c.connections]
   [dinsro.specs]))

(>defn index-ids
  []
  [=> (s/coll-of :xt/id)]
  (c.xtdb/query-values '{:find [?e] :where [[?e ::m.c.connections/name _]]}))
