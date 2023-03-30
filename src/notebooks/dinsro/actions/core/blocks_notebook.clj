(ns dinsro.actions.core.blocks-notebook
  (:require [dinsro.actions.core.blocks :as a.c.blocks]
            [dinsro.queries.core.nodes :as q.c.nodes]))

(comment
  (def node-id (first (q.c.nodes/index-ids)))

  (a.c.blocks/fetch-block-by-height node-id 2))