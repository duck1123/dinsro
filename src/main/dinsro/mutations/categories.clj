(ns dinsro.mutations.categories
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defn do-create
  [identity name]
  (if-let [_user-eid (q.users/find-eid-by-id identity)]
    (let [params {::m.categories/name name
                  ::m.categories/user {::m.users/id identity}}]
      (if-let [record (q.categories/create-record params)]
        {:status           :success
         :created-category [{::m.categories/id (:db/id record)}]}
        {:status :failure}))
    {:status :no-user}))

(defmutation create!
  [{{{:keys [identity]} :session} :request} {::m.categories/keys [name]}]
  {::pc/params #{::m.categories/name}
   ::pc/output [:status
                :created-category [::m.categories/id]]}
  (do-create identity name))

(defn do-delete
  [id]
  (or (when-not (or (nil? id) (empty? id))
        (when-let [eid (q.categories/find-eid-by-id id)]
          (q.categories/delete-record eid)
          {:status :success}))
      {:status :failure}))

(defmutation delete!
  [_request {::m.categories/keys [id]}]
  {::pc/params #{::m.categories/id}
   ::pc/output [:status :message]}
  (do-delete id))

(def resolvers
  [create!
   delete!])
