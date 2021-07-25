(ns dinsro.mutations.currencies
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc #?@(:clj [:refer [defmutation]])]
   [dinsro.model.currencies :as m.currencies]
   #?(:clj [dinsro.queries.currencies :as q.currencies])
   #?(:clj [dinsro.queries.users :as q.users])
   [taoensso.timbre :as log]))

(comment ::m.currencies/_ ::pc/_)

#?(:clj
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
             (log/warn "failed to create currency")
             {:status           :failure
              :created-currency []})))
       {:status :no-user})))

#?(:clj
   (defn do-delete
     [id]
     (let [eid (q.currencies/find-eid-by-id id)]
       (q.currencies/delete-record eid))
     {:status :success}))

#?(:clj
   (defmutation create!
     [{{{:keys [identity]} :session} :request} {::m.currencies/keys [id name]}]
     {::pc/params #{::m.currencies/id ::m.currencies/name}
      ::pc/output [:status
                   :created-currency [::m.currencies/id]]}
     (do-create id name identity))
   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (defmutation delete!
     [_request {::m.currencies/keys [id]}]
     {::pc/params #{::m.currencies/id}
      ::pc/output [:status :message]}
     (do-delete id))
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#(:clj
  (def resolvers
    [create!
     delete!]))
