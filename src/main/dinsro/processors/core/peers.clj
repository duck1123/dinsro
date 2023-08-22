(ns dinsro.processors.core.peers
  (:require
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations :as mu]
   [dinsro.responses.core.peers :as r.c.peers]
   [lambdaisland.glogc :as log]))

;; [[../../actions/core/peers.clj]]
;; [[../../model/core/peers.cljc]]
;; [[../../mutations/core/peers.cljc]]
;; [[../../responses/core/peers.cljc]]

(def model-key ::m.c.peers/id)

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.c.peers/delete! id)
    {::mu/status                 :ok
     ::r.c.peers/deleted-records (m.c.peers/idents [id])}))
