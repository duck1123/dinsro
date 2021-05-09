(ns dinsro.mutations.accounts
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [taoensso.timbre :as log]))

(defn do-create
  [name currency-id user-id initial-value]

  (let [params {::m.accounts/name          name
                ::m.accounts/currency      {::m.currencies/id currency-id}
                ::m.accounts/initial-value initial-value
                ::m.accounts/user          {::m.users/id user-id}}]
    (if-let [eid (q.accounts/create-record params)]
      (let [record (q.accounts/read-record eid)]
        {:status :success
         :items  [(m.accounts/ident-item record)]})
      {:status :failure}))
  {:status :no-user})

(defn do-delete
  [id]
  (q.accounts/delete-record id)
  {:status :success})

(defmutation create!
  [{{{:keys [identity]} :session} :request} params]
  {::pc/params #{::m.accounts/name}
   ::pc/output [:status
                :items [::m.accounts/id]]}
  (let [{::m.accounts/keys               [name initial-value]
         {currency-id ::m.currencies/id} ::m.accounts/currency} params]
    (do-create name currency-id identity initial-value)))

(defmutation delete!
  [_env {::m.accounts/keys [id]}]
  {::pc/params #{::m.accounts/id}
   ::pc/output [:status]}
  (do-delete id))

(def resolvers
  [create!
   delete!])
