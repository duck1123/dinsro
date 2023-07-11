(ns dinsro.processors.transactions
  (:require
   [dinsro.actions.transactions :as a.transactions]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../actions/transactions.clj]]
;; [[../model/transactions.cljc]]

(def model-key ::m.transactions/id)

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (if-let [id (get props model-key)]
    (do
      (a.transactions/delete! id)
      {::mu/status :ok})
    {::mu/status :fail ::mu/message "No id"}))
