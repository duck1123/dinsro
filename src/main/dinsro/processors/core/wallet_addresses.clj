(ns dinsro.processors.core.wallet-addresses
  (:require
   [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.mutations :as mu]
   [dinsro.responses.core.wallet-addresses :as r.c.wallet-addresses]
   [lambdaisland.glogc :as log]))

;; [[../../actions/core/wallet-addresses.clj]]
;; [[../../mutations/core/wallet-addresses.cljc]]

(def model-key ::m.c.wallet-addresses/id)

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.c.wallet-addresses/delete! id)
    {::mu/status :ok
     ::r.c.wallet-addresses/deleted-records (m.c.wallet-addresses/idents [id])}))
