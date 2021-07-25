(ns dinsro.mutations.rate-sources
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc #?@(:clj [:refer [defmutation]])]
   [dinsro.model.rate-sources :as m.rate-sources]
   #?(:clj [dinsro.queries.rate-sources :as q.rate-sources])
   [taoensso.timbre :as log]))

(comment ::pc/_ ::m.rate-sources/_)

#?(:clj
   (defn do-create
     [params]
     (if-let [record (q.rate-sources/create-record params)]
       {:status :success
        :item   [(m.rate-sources/ident (:db/id record))]}
       {:status :failure})))

#?(:clj
   (defn do-delete
     [id]
     (q.rate-sources/delete-record id)
     {:status :success}))

#?(:clj
   (defmutation create!
     [_env params]
     {::pc/params #{:name :url :currency-id}
      ::pc/output [:status {:item [::m.rate-sources/id]}]}
     (do-create params))
   :cljs

   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (defmutation delete!
     [_request {::m.rate-sources/keys [id]}]
     {::pc/params #{::m.rate-sources/id}
      ::pc/output [:status]}
     (do-delete id))
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [create! delete!]))
