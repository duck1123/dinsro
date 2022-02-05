(ns dinsro.mutations.core-tx
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core-tx :as a.core-tx])
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-tx :as m.core-tx]))

(comment ::pc/_ ::m.core-block/_ ::m.core-tx/_)

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.core-tx/id}
      ::pc/output [:status]}
     (a.core-tx/fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [fetch!]))
