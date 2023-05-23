(ns dinsro.processors.contacts
  (:require
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.mutations :as mu]
   [dinsro.responses.contacts :as r.contacts]
   [lambdaisland.glogc :as log]))

;; [../actions/contacts.clj]
;; [../mutations/contacts.cljc]

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [{::m.contacts/keys [id]} props]
    (a.contacts/delete! id)
    {::mu/status :ok ::r.contacts/deleted-records (m.contacts/idents [id])}))
