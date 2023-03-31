(ns dinsro.processors.nostr.runs
  (:require
   [dinsro.actions.nostr.runs :as a.n.runs]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(defn delete!
  [props]
  (log/info :do-delete!/starting {:props props})
  (if-let [run-id (::m.n.runs/id props)]
    (try
      (a.n.runs/delete! run-id)
      {::mu/status :ok}
      (catch Exception ex
        (mu/exception-response ex)))
    (mu/error-response "No run id")))

(defn stop!
  [props]
  (log/info :stop!/starting {:props props})
  (if-let [run-id (::m.n.runs/id props)]
    (try
      (a.n.runs/stop! run-id)
      {::mu/status :ok}
      (catch Exception ex
        (mu/exception-response ex)))
    (mu/error-response "No run id")))
