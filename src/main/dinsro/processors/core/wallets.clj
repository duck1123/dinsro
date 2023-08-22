(ns dinsro.processors.core.wallets
  (:require
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations :as mu]
   [dinsro.responses.core.wallets :as r.c.wallets]))

;; [[../../actions/core/wallets.clj]]

(def model-key ::m.c.wallets/id)

(defn roll!
  [props]
  (let [{::m.c.wallets/keys [id]} props
        response                  (a.c.wallets/roll! props)]
    (comment id)
    {::mu/status        :ok
     ::m.c.wallets/item response}))

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.c.wallets/delete! id)
    {::mu/status :ok
     ::r.c.wallets/deleted-records (m.c.wallets/idents [id])}))
