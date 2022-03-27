(ns dinsro.mutations.core.addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.addresses :as a.core-addresses])
   [dinsro.model.core.addresses :as m.core-addresses]))

(comment ::pc/_ ::m.core-addresses/_)

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.core-addresses/keys [id]}]
     {::pc/params #{::m.core-addresses/id}
      ::pc/output [:status]}
     (a.core-addresses/fetch! id))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)
     (ok-action [env]
       (let [body (get-in env [:result :body])]
         (get body `fetch!)
         {}))))

#?(:clj (def resolvers [fetch!]))
