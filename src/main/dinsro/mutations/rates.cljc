(ns dinsro.mutations.rates
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc #?@(:clj [:refer [defmutation]])]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])
   [taoensso.timbre :as log]))

(comment ::m.rates/_ ::pc/_)

#?(:clj
   (defn do-create
     [_params]
     {}))

#?(:clj
   (defn do-delete
     [id]
     (q.rates/delete-record id)
     {:status :success}))

#?(:clj
   (defmutation create!
     [_env params]
     {::pc/params #{::m.rates/value}
      ::pc/output [:status
                   :items [::m.rates/id]]}
     (do-create params))
   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (defmutation delete!
     [_env {::m.rates/keys [id]}]
     {::pc/params #{::m.rates/id}
      ::pc/output [:status]}
     (do-delete id))
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [create! delete!]))
