(ns dinsro.processors.ln.channels
  (:require
   [dinsro.actions.ln.channels :as a.ln.channels]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.channels :as r.ln.channels]))

;; [[../../actions/ln/channels.clj]]

(def model-key ::m.ln.channels/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.channels/delete! id)
    {::mu/status                     :ok
     ::r.ln.channels/deleted-records (m.ln.channels/idents [id])}))
