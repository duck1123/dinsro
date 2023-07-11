(ns dinsro.processors.contacts
  (:require
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.mutations :as mu]
   [dinsro.responses.contacts :as r.contacts]
   [lambdaisland.glogc :as log]))

;; [[../actions/contacts.clj]]
;; [[../mutations/contacts.cljc]]
;; [[../ui/contacts.cljs]]

(defn create!
  [{:keys [query-params]} props]
  (log/info :create!/starting {:props props :query-params query-params})
  (let [actor-id (:actor/id query-params)
        props    (assoc props ::m.contacts/user actor-id)
        props    (dissoc props ::m.contacts/id)
        id       (a.contacts/create! props)
        ids      [id]
        idents   (m.contacts/idents ids)]
    {::mu/status                  :ok
     ::r.contacts/created-records idents}))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [{::m.contacts/keys [id]} props]
    (a.contacts/delete! id)
    {::mu/status :ok ::r.contacts/deleted-records (m.contacts/idents [id])}))
