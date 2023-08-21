(ns dinsro.processors.nostr.requests
  (:refer-clojure :exclude [run!])
  (:require
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.requests :as r.n.requests]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/requests.clj]]
;; [[../../mutations/nostr/requests.cljc]]

(def model-key ::m.n.requests/id)

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.requests/delete! id)
    {::mu/status                   :ok
     ::r.n.requests/deleted-records (m.n.requests/idents [id])}))

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
