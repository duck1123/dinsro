(ns dinsro.processors.nostr.requests
  (:refer-clojure :exclude [run!])
  (:require
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [lambdaisland.glogc :as log]))

;; [[../../mutations/nostr/requests.cljc]]

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
