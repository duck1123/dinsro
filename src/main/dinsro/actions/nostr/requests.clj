(ns dinsro.actions.nostr.requests
  (:require
   [dinsro.model.nostr.requests :as m.n.requests]
   [lambdaisland.glogc :as log]))

(defn do-stop!
  [params]
  (if-let [request-id (::m.n.requests/id params)]
    (do
      (log/info :do-stop!/starting {:request-id request-id})
      {:status "ok"})
    (throw (RuntimeException. "No request id"))))
