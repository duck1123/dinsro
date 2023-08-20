(ns dinsro.actions.currencies
  (:require
   [dinsro.queries.currencies :as q.currencies]
   [lambdaisland.glogc :as log]))

;; [[../joins/currencies.cljc]]
;; [[../model/currencies.cljc]]
;; [[../mutations/currencies.cljc]]
;; [[../processors/currencies.clj]]
;; [[../queries/currencies.clj]]
;; [[../ui/admin/currencies.cljs]]
;; [[../../../notebooks/dinsro/notebooks/currencies_notebook.clj]]

(defn delete!
  [currency-id]
  (log/info :delete!/starting {:currency-id currency-id})
  (q.currencies/delete! currency-id))
