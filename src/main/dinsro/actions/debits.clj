(ns dinsro.actions.debits
  (:require [dinsro.queries.debits :as q.debits]))

(comment

  (def positive? true)

  (q.debits/index-ids {:positive? false})

  (q.debits/get-index-params {:positive? positive?})
  (q.debits/get-index-query {:positive? positive?})

  nil)
