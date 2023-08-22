(ns dinsro.actions.ln.invoices
  (:require
   [dinsro.queries.ln.invoices :as q.ln.invoices]
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.ln.invoices/delete! id))
