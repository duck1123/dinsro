(ns dinsro.actions.debits
  (:require
   [dinsro.queries.debits :as q.debits]
   [taoensso.timbre :as log]))

;; [[../joins/debits.cljc]]
;; [[../model/debits.cljc]]
;; [[../queries/debits.clj]]
;; [[../../../notebooks/dinsro/notebooks/debits_notebook.clj]]

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.debits/delete! id))
