(ns dinsro.processors.ln.nodes
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.nodes :as r.ln.nodes]))

;; [[../../actions/ln/nodes.clj]]

(def model-key ::m.ln.nodes/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.nodes/delete! id)
    {::mu/status                  :ok
     ::r.ln.nodes/deleted-records (m.ln.nodes/idents [id])}))
