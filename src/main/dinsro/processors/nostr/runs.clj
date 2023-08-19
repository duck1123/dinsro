(ns dinsro.processors.nostr.runs
  (:require
   [dinsro.actions.nostr.runs :as a.n.runs]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.runs :as r.n.runs]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/runs.clj]]
;; [[../../model/nostr/runs.cljc]]
;; [[../../mutations/nostr/runs.cljc]]

(def model-key ::m.n.runs/id)

(defn delete!
  [_env props]
  (log/info :do-delete!/starting {:props props})
  (if-let [id (model-key props)]
    (try
      (a.n.runs/delete! id)
      {::mu/status                :ok
       ::r.n.runs/deleted-records (m.n.runs/idents [id])}
      (catch Exception ex
        (mu/exception-response ex)))
    (mu/error-response "No run id")))

(defn stop!
  [props]
  (log/info :stop!/starting {:props props})
  (if-let [id (model-key props)]
    (try
      (a.n.runs/stop! id)
      (log/info :stop!/finished {})
      {::mu/status :ok}
      (catch Exception ex
        (log/error :stop!/errored {:ex ex})
        (mu/exception-response ex)))
    (mu/error-response "No run id")))
