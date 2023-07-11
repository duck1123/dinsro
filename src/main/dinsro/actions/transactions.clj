(ns dinsro.actions.transactions
  (:require
   [dinsro.queries.transactions :as q.transactions]
   [lambdaisland.glogc :as log]))

;; [[../model/transactions.cljc]]
;; [[../processors/transactions.clj]]
;; [[../queries/transactions.clj]]

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.transactions/delete! id))

(comment

  (q.transactions/index-ids {})

  nil)
