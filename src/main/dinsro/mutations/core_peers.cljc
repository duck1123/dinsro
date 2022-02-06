(ns dinsro.mutations.core-peers
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core-peers :as a.core-peers])
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-tx :as m.core-tx]))

(comment ::pc/_ ::m.core-block/_ ::m.core-tx/_)

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{::m.core-tx/id}
      ::pc/output [:status]}
     (a.core-peers/create! props))

   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [create!]))
