(ns dinsro.mutations.currencies
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defmutation create!
  [{{{:keys [identity]} :session} :request} {::m.currencies/keys [name]}]
  {::pc/params #{::m.currencies/name}
   ::pc/output [:status
                :created-category [::m.currencies/id]]}
  (if-let [_user-eid (q.users/find-eid-by-username identity)]
    (let [params  #::m.currencies{:name name :user {::m.users/username identity}}]
      (if-let [record (q.currencies/create-record params)]
        {:status           :success
         :created-category [{::m.currencies/id (::m.currencies/id record)}]}
        (do
          (timbre/warn "failed to create currency")
          {:status :failure})))
    {:status :no-user}))

(defmutation delete!
  [_request {::m.currencies/keys [id]}]
  {::pc/params #{::m.currencies/id}
   ::pc/output [:status :message]}
  (if (zero? id)
    {:status  :failure
     :message "Bitcoin cannot be killed"}
    (do
      (q.currencies/delete-record id)
      {:status :success})))

(def resolvers
  [create!
   delete!])
