(ns dinsro.processors.ln.peers
  (:require
   [dinsro.actions.ln.peers :as a.ln.peers]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.peers :as r.ln.peers]))

;; [[../../actions/ln/peers.clj]]

(def model-key ::m.ln.peers/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.peers/delete! id)
    {::mu/status                  :ok
     ::r.ln.peers/deleted-records (m.ln.peers/idents [id])}))
