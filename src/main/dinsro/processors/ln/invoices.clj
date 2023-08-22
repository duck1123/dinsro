(ns dinsro.processors.ln.invoices
  (:require
   [dinsro.actions.ln.invoices :as a.ln.invoices]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.invoices :as r.ln.invoices]))

;; [[../../actions/ln/invoices.clj]]

(def model-key ::m.ln.invoices/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.invoices/delete! id)
    {::mu/status                     :ok
     ::r.ln.invoices/deleted-records (m.ln.invoices/idents [id])}))
