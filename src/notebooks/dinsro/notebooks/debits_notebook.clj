^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.debits-notebook
  (:require
   [dinsro.model.debits :as m.debits]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.debits :as q.debits]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; [[../../../main/dinsro/actions/debits.clj]]

;; # Debits

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## Generated item

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.debits/item)

^{::clerk/viewer clerk/table}
(map q.debits/read-record (q.debits/index-ids))

(def transaction-ids (q.transactions/index-ids))

^{::clerk/viewer clerk/table}
(map
 (fn [transaction-id]
   {:transaction-id transaction-id
    :debits         (q.debits/find-by-transaction transaction-id)})
 transaction-ids)

(comment

  (def positive? true)

  (q.debits/index-ids {:positive? positive?})

  (q.debits/count-ids {:positive? false})
  (q.debits/index-ids)

  (def debit-id (first (q.debits/index-ids)))

  (q.debits/read-record debit-id)

  (q.rates/read-record (q.rates/find-for-debit debit-id))

  nil)
