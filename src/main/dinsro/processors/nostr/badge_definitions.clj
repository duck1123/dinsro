(ns dinsro.processors.nostr.badge-definitions
  (:require
   [dinsro.actions.nostr.badge-definitions :as a.n.badge-definitions]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.badge-definitions :as r.n.badge-definitions]
   [lambdaisland.glogc :as log]))

;; [[../../mutations/nostr/badge_definitions.cljc]]

(def model-key ::m.n.badge-definitions/id)

(defn do-fetch-definitions!
  [props]
  (log/info :do-fetch-definitions!/starting {:props props}))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.badge-definitions/delete! id)
    {::mu/status                   :ok
     ::r.n.badge-definitions/deleted-records (m.n.badge-definitions/idents [id])}))
