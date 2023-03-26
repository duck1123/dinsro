(ns dinsro.actions.currencies
  (:require
   [dinsro.model.currencies :as m.currencies]
   [dinsro.mutations :as mu]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs.currencies :as s.currencies]
   [lambdaisland.glogc :as log]))

(defn delete!
  [currency-id]
  (q.currencies/delete! currency-id))

(defn do-delete!
  [_env props]
  (log/info :do-delete!/starting {:props props})
  (try
    (mu/error-response "Not Implemented")
    (if-let [currency-id (::m.currencies/id props)]
      (do
        (delete! currency-id)
        {::mu/status                    :ok
         ::s.currencies/deleted-records [(m.currencies/ident currency-id)]})
      (mu/error-response "No ID"))
    (catch Exception ex (mu/exception-response ex))))

(comment

  (q.currencies/index-ids)

  (q.currencies/read-record (first (q.currencies/index-ids)))
  (q.currencies/index-records)

  nil)
