(ns dinsro.mutations.ln.peers
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.peers :as a.ln.peers])
   [dinsro.model.core.blocks :as m.ln.blocks]
   [dinsro.model.core.peers :as m.ln.peers]
   #?(:clj [lambdaisland.glogc :as log])))

(comment ::pc/_ ::m.ln.blocks/_ ::m.ln.peers/_)

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{::m.ln.peers/id}
      ::pc/output [:status]}
     (a.ln.peers/create! props))

   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.ln.peers/id}
      ::pc/output [:status]}
     (log/debug :delete/starting {:props props})
     (a.ln.peers/delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [create! delete!]))
