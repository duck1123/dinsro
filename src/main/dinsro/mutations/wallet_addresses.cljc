(ns dinsro.mutations.wallet-addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.wallet-addresses :as a.wallet-addresses])
   [dinsro.model.wallet-addresses :as m.wallet-addresses]
   #?(:clj [dinsro.queries.wallet-addresses :as q.wallet-addresses])))

(comment ::m.wallet-addresses/_ ::pc/_)

#?(:clj
   (pc/defmutation generate!
     [_env {::m.wallet-addresses/keys [id]}]
     {::pc/params #{::m.wallet-addresses/id}
      ::pc/output [:status]}
     (let [node (q.wallet-addresses/read-record id)]
       (a.wallet-addresses/generate! node)
       {:status :ok}))

   :cljs
   (defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [generate!]))
