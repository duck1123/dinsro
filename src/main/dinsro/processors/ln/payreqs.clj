(ns dinsro.processors.ln.payreqs
  (:require
   [dinsro.actions.ln.payreqs :as a.ln.payreqs]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.payreqs :as r.ln.payreqs]))

;; [[../../actions/ln/payreqs.clj]]

(def model-key ::m.ln.payreqs/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.payreqs/delete! id)
    {::mu/status                    :ok
     ::r.ln.payreqs/deleted-records (m.ln.payreqs/idents [id])}))
