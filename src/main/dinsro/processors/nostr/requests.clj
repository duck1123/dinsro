(ns dinsro.processors.nostr.requests
  (:refer-clojure :exclude [run!])
  (:require
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

(defn run!
  [props]
  (log/info :run!/starting {:props props})
  (let [request-id (::m.n.requests/id props)]
    (log/info :run!/starting {:request-id request-id})
    (mu/error-response "Not Implemented")))

(defn start!
  [params]
  (if-let [request-id (::m.n.requests/id params)]
    (do
      (comment request-id)
      ;; {::mu/status :ok}
      (mu/error-response "Not Implemented"))
    (throw (ex-info "No request id" {}))))

(defn stop!
  [params]
  (if-let [request-id (::m.n.requests/id params)]
    (do
      (log/info :stop!/starting {:request-id request-id})
      (try
        (a.n.requests/stop! request-id)
        {::mu/status "ok"}
        (catch Exception ex (mu/exception-response ex))))
    (mu/error-response "No Request id")))
