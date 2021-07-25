(ns dinsro.mutations.users
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc #?@(:clj [:refer [defmutation]])]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.users :as q.users])
   [taoensso.timbre :as log]))

(comment ::m.users/_ ::pc/_)

#?(:cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (defn do-delete
     [id]
     (q.users/delete-record id)
     {:status :success}))

#?(:clj
   (defmutation delete!
     [_request {::m.users/keys [id]}]
     {::pc/params #{::m.users/id}
      ::pc/output [:status]}
     (do-delete id))
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [delete!]))
