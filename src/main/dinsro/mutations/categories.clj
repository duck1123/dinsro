(ns dinsro.mutations.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defn do-create
  [identity params]
  (let [{::m.categories/keys [name]} params]
    (if-let [_user-eid (q.users/find-id-by-username identity)]
      (let [params {::m.categories/name name
                    ::m.categories/user {::m.users/username identity}}]
        (if-let [record (q.categories/create-record params)]
          {:status           :success
           :created-category [{::m.categories/id (:db/id record)}]}
          {:status :failure}))
      {:status :no-user})))

(defmutation create!
  [{{{:keys [identity]} :session} :request} params]
  {::pc/params #{::m.categories/name}
   ::pc/output [:status
                :created-category [::m.categories/id]]}
  (do-create identity params))

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
