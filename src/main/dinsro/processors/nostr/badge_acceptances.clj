(ns dinsro.processors.nostr.badge-acceptances
  (:require
   [dinsro.actions.nostr.badge-acceptances :as a.n.badge-acceptances]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.badge-acceptances :as r.n.badge-acceptances]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/badge_acceptances.clj]]
;; [[../../model/nostr/badge_acceptances.cljc]]
;; [[../../mutations/nostr/badge_acceptances.cljc]]

(def model-key ::m.n.badge-acceptances/id)

(defn fetch!
  [props]
  (let [{relay-id ::m.n.relays/id
         pubkey-id ::m.n.pubkeys/id} props]
    (a.n.badge-acceptances/fetch! relay-id pubkey-id)
    {::mu/status :ok}))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.badge-acceptances/delete! id)
    {::mu/status                   :ok
     ::r.n.badge-acceptances/deleted-records (m.n.badge-acceptances/idents [id])}))
