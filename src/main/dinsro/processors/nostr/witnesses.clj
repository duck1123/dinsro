(ns dinsro.processors.nostr.witnesses
  (:require
   [dinsro.actions.nostr.witnesses :as a.n.witnesses]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.witnesses :as r.n.witnesses]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/witnesses.clj]]
;; [[../../model/nostr/witnesses.cljc]]
;; [[../../mutations/nostr/witnesses.cljc]]
;; [[../../responses/nostr/witnesses.cljc]]

(def model-key ::m.n.witnesses/id)

(defn delete!
  [_env props]
  (log/info :do-delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.witnesses/delete! id)
    {::mu/status :ok
     ::r.n.witnesses/deleted-records (m.n.witnesses/idents [id])}))
