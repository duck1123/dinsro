(ns dinsro.processors.ln.accounts
  (:require
   [dinsro.actions.ln.accounts :as a.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.accounts :as r.ln.accounts]))

;; [[../../actions/ln/accounts.clj]]

(def model-key ::m.ln.accounts/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.accounts/delete! id)
    {::mu/status                     :ok
     ::r.ln.accounts/deleted-records (m.ln.accounts/idents [id])}))
