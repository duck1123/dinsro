(ns dinsro.actions.debits
  (:require
   [dinsro.queries.debits :as q.debits]
   [dinsro.queries.rates :as q.rates]
   [taoensso.timbre :as log]))

;; [../joins/debits.cljc]
;; [../model/debits.cljc]
;; [../queries/debits.clj]

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.debits/delete! id))

(comment

  (def positive? true)

  (q.debits/index-ids {:positive? positive?})

  (q.debits/count-ids {:positive? false})
  (q.debits/index-ids)

  (def debit-id (first (q.debits/index-ids)))

  (q.debits/read-record debit-id)

  (q.rates/read-record (q.rates/find-for-debit debit-id))

  nil)
