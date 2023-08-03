(ns dinsro.processors.instances
  (:require
   [dinsro.actions.instances :as a.instances]
   [dinsro.model.instances :as m.instances]
   [dinsro.mutations :as mu]
   [dinsro.responses.instances :as r.instances]
   [lambdaisland.glogc :as log]))

;; [[../../../notebooks/dinsro/notebooks/instances_notebook.clj]]

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [{::m.instances/keys [id]} props]
    (a.instances/delete! id)
    {::mu/status :ok ::r.instances/deleted-records (m.instances/idents [id])}))
