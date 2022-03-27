(ns dinsro.mutations.core.peers
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.peers :as a.core-peers])
   [dinsro.model.core.blocks :as m.core-block]
   [dinsro.model.core.peers :as m.core-peers]
   #?(:clj [lambdaisland.glogc :as log])))

(comment ::pc/_ ::m.core-block/_ ::m.core-peers/_)

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{::m.core-peers/id}
      ::pc/output [:status]}
     (a.core-peers/create! props))

   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.core-peers/id}
      ::pc/output [:status]}
     (log/debug :delete/starting {:props props})
     (a.core-peers/delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [create! delete!]))
