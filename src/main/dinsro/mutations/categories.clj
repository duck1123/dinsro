(ns dinsro.mutations.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation create!
  [{{{:keys [identity]} :session} :request} {::m.categories/keys [name]}]
  {::pc/params #{::m.categories/name}
   ::pc/output [:status
                :created-category [::m.categories/id]]}
  (if-let [user-eid (q.users/find-id-by-email identity)]
    (let [user-id (q.users/find-id-by-eid user-eid)
          params  {::m.categories/name name
                   ::m.categories/user {::m.users/id user-id}}]
      (if-let [record (q.categories/create-record params)]
        {:status           :success
         :created-category [{::m.categories/id (:db/id record)}]}
        {:status :failure}))
    {:status :no-user}))

(defmutation delete!
  [_request {::m.categories/keys [id]}]
  {::pc/params #{::m.categories/id}
   ::pc/output [:status :message]}
  (if (zero? id)
    {:status :failure}
    (do
      (q.categories/delete-record id)
      {:status :success})))

(def resolvers
  [create!
   delete!])
