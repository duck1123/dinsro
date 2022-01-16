(ns dinsro.mutations.core-address
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core-address :as a.core-address])
   [dinsro.model.core-address :as m.core-address]
   [taoensso.timbre :as log]))

(comment ::pc/_ ::m.core-address/_)

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.core-address/keys [id]}]
     {::pc/params #{::m.core-address/id}
      ::pc/output [:status]}
     (a.core-address/fetch! id))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)
     (ok-action [env]
       (let [body (get-in env [:result :body])]
         (get body `fetch!)
         {}))))

#?(:clj (def resolvers [fetch!]))
