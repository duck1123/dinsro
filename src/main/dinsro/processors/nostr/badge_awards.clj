(ns dinsro.processors.nostr.badge-awards
  (:require
   [dinsro.actions.nostr.badge-awards :as a.n.badge-awards]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.badge-awards :as r.n.badge-awards]
   [lambdaisland.glogc :as log]))

;; [[../../mutations/nostr/badge_awards.cljc]]

(def model-key ::m.n.badge-awards/id)

(defn fetch!
  [props]
  (log/info :fetch!/starting {:props props})
  (mu/error-response "Not Implemented"))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.badge-awards/delete! id)
    {::mu/status                   :ok
     ::r.n.badge-awards/deleted-records (m.n.badge-awards/idents [id])}))
