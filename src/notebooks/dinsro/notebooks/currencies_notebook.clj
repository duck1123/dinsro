(ns dinsro.notebooks.currencies-notebook
  (:require
   [dinsro.queries.currencies :as q.currencies]))

;; [[../../../main/dinsro/queries/currencies.clj]]
;; [[../../../main/dinsro/ui/admin/currencies.cljc]]

(comment

  (q.currencies/index-ids)

  (q.currencies/read-record (first (q.currencies/index-ids)))
  (q.currencies/index-records)

  nil)
