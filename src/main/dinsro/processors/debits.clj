(ns dinsro.processors.debits
  (:require
   [dinsro.actions.debits :as a.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.mutations :as mu]
   [dinsro.responses.debits :as r.debits]
   [lambdaisland.glogc :as log]))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [{::m.debits/keys [id]} props]
    (a.debits/delete! id)
    {::mu/status :ok ::r.debits/deleted-records (m.debits/idents [id])}))
