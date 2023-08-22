(ns dinsro.processors.core.connections
  (:require
   [dinsro.actions.core.connections :as a.c.connections]
   [dinsro.model.core.connections :as m.c.connections]
   [dinsro.mutations :as mu]
   [dinsro.responses.core.connections :as r.c.connections]))

;; [[../../actions/core/connections.clj]]
;; [[../../mutations/core/connections.cljc]]

(def model-key ::m.c.connections/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.c.connections/delete! id)
    {::mu/status                       :ok
     ::r.c.connections/deleted-records (m.c.connections/idents [id])}))
