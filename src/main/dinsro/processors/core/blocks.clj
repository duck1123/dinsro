(ns dinsro.processors.core.blocks
  (:require
   [dinsro.actions.core.blocks :as a.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.mutations :as mu]
   [dinsro.responses.core.blocks :as r.c.blocks]))

;; [[../../actions/core/blocks.clj]]
;; [[../../mutations/core/blocks.cljc]]

(def model-key ::m.c.blocks/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.c.blocks/delete! id)
    {::mu/status                  :ok
     ::r.c.blocks/deleted-records (m.c.blocks/idents [id])}))
