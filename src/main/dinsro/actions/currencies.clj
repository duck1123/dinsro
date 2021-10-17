(ns dinsro.actions.currencies
  (:require
   ;; [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]))

(comment

  (q.currencies/index-ids)

  (q.currencies/read-record (first (q.currencies/index-ids)))
  (q.currencies/index-records)

  nil)
