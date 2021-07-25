(ns dinsro.mutations.categories
  (:require
   #?(:clj [com.wsscode.pathom.connect :as pc :refer [defmutation]]
      :cljs [com.wsscode.pathom.connect :as pc])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.users :as q.users])
   [taoensso.timbre :as log]))

(comment ::m.categories/_ ::m.users/_ ::pc/_)

#?(:clj
   (defn do-create
     [identity name]
     (if-let [_user-eid (q.users/find-eid-by-id identity)]
       (let [params {::m.categories/name name
                     ::m.categories/user {::m.users/id identity}}]
         (if-let [record (q.categories/create-record params)]
           {:status           :success
            :created-category [{::m.categories/id (:db/id record)}]}
           {:status :failure}))
       {:status :no-user})))

#?(:clj
   (defmutation create!
     [{{{:keys [identity]} :session} :request} {::m.categories/keys [name]}]
     {::pc/params #{::m.categories/name}
      ::pc/output [:status
                   :created-category [::m.categories/id]]}
     (do-create identity name))
   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (defn do-delete
     [id]
     (or (when-not (or (nil? id) (empty? id))
           (when-let [eid (q.categories/find-eid-by-id id)]
             (q.categories/delete-record eid)
             {:status :success}))
         {:status :failure})))

#?(:clj
   (defmutation delete!
     [_request {::m.categories/keys [id]}]
     {::pc/params #{::m.categories/id}
      ::pc/output [:status :message]}
     (do-delete id))
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [create!
      delete!]))
