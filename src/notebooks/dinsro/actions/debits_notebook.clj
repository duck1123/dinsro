^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.debits-notebook
  (:require
   [dinsro.queries.debits :as q.debits]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

^{::clerk/viewer clerk/table}
(map q.debits/read-record (q.debits/index-ids))

(def transaction-ids (q.transactions/index-ids))

^{::clerk/viewer clerk/table}
(map
 (fn [transaction-id]
   {:transaction-id transaction-id
    :debits         (q.debits/find-by-transaction transaction-id)})
 transaction-ids)
