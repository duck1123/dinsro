(ns dinsro.mutations.accounts
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defn do-create
  [identity params]
  (if-let [user-id (q.users/find-eid-by-id identity)]
    (let [currency-id (get-in params [::m.accounts/currency :db/id])
          params      (assoc-in params [::m.accounts/user :db/id] user-id)
          params      (if (zero? currency-id)
                        (dissoc params ::m.accounts/currency)
                        params)]
      (if-let [record (q.accounts/create-record params)]
        {:status :success
         :items  [{::m.accounts/id (:db/id record)}]}
        {:status :failure}))
    {:status :no-user}))

(defn do-delete
  [id]
  (q.accounts/delete-record id)
  {:status :success})

(defmutation create!
  [{{{:keys [identity]} :session} :request} params]
  {::pc/params #{::m.accounts/name}
   ::pc/output [:status
                :items [::m.accounts/id]]}
  (do-create identity params))

(defmutation delete!
  [_env {::m.accounts/keys [id]}]
  {::pc/params #{::m.accounts/id}
   ::pc/output [:status]}
  (do-delete id))

(def resolvers
  [create!
   delete!])
