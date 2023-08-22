(ns dinsro.processors.ln.payments
  (:require
   [dinsro.actions.ln.payments :as a.ln.payments]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.mutations :as mu]
   [dinsro.responses.ln.payments :as r.ln.payments]))

;; [[../../actions/ln/payments.clj]]

(def model-key ::m.ln.payments/id)

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.ln.payments/delete! id)
    {::mu/status                     :ok
     ::r.ln.payments/deleted-records (m.ln.payments/idents [id])}))
