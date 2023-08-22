(ns dinsro.processors.ln.remote-nodes
  (:require
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.remote-nodes :as r.ln.remote-nodes]))

;; [[../../actions/ln/remote_nodes.clj]]

(def model-key ::m.ln.remote-nodes/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.remote-nodes/delete! id)
    {::mu/status                         :ok
     ::r.ln.remote-nodes/deleted-records (m.ln.remote-nodes/idents [id])}))
