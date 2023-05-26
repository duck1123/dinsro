(ns dinsro.processors.core.wallets
  (:require
   [dinsro.actions.core.wallets :as a.c.wallets]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations :as mu]))

(defn roll!
  [props]
  (let [{::m.c.wallets/keys [id]} props
        response                  (a.c.wallets/roll! props)]
    (comment id)
    {::mu/status        :ok
     ::m.c.wallets/item response}))
