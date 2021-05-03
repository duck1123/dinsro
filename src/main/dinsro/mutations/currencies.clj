(ns dinsro.mutations.currencies
  (:require
   [com.wsscode.pathom.connect :as pc :refer [defmutation]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as timbre]))

(defn do-create
  [id name identity]
  (if-let [_user-eid (q.users/find-eid-by-id identity)]
    (let [_can-create? true ;; should be admin
          params       #::m.currencies{:id   id
                                       :name name}]
      (if-let [_record (q.currencies/create-record params)]
        {:status           :success
         :created-currency [{::m.currencies/id id}]}
        (do
          (timbre/warn "failed to create currency")
          {:status           :failure
           :created-currency []})))
    {:status :no-user}))

(defn do-delete
  [id]
  (let [eid (q.currencies/find-eid-by-id id)]
    (q.currencies/delete-record eid))
  {:status :success})

(defmutation create!
  [{{{:keys [identity]} :session} :request} {::m.currencies/keys [id name]}]
  {::pc/params #{::m.currencies/id ::m.currencies/name}
   ::pc/output [:status
                :created-currency [::m.currencies/id]]}
  (do-create id name identity))

(defmutation delete!
  [_request {::m.currencies/keys [id]}]
  {::pc/params #{::m.currencies/id}
   ::pc/output [:status :message]}
  (do-delete id))

(def resolvers
  [create!
   delete!])
