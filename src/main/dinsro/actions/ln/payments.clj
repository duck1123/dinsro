(ns dinsro.actions.ln.payments
  (:require
   [dinsro.queries.ln.payments :as q.ln.payments]
   [lambdaisland.glogc :as log]))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.ln.payments/delete! id))
