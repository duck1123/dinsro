(ns dinsro.actions.users
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]
   [dinsro.queries.users :as q.users]
   [lambdaisland.glogc :as log]))

(defn set-role!
  [user-id role]
  (log/info :set-role!/starting {:user-id user-id :role role})
  (if-let [user (q.users/read-record user-id)]
    (do
      (log/finer :set-role/user-found {:user user})
      (q.users/update! user-id {::m.users/role role}))
    (do
      (log/error :set-role!/user-not-read {:user-id user-id})
      (throw (RuntimeException. "user not found")))))

(defn delete!
  [id]
  (q.users/delete! id))

(defn do-delete!
  [_env props]
  (log/info :do-delete!/starting {:props props})
  (let [{::m.users/keys [id]} props
        ids [id]]
    (delete! id)
    {::mu/status        :ok
     ::mu/deleted-items (m.users/idents ids)}))
