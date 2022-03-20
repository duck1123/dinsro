(ns dinsro.mutations.core.connections
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.connections :as a.c.connections])
   [dinsro.model.core.connections :as m.c.connections]))

(comment ::pc/_ ::m.c.connections/_)

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{::m.c.connections/id}
      ::pc/output [:status]}
     (a.c.connections/create! props))

   :cljs
   (defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [create!]))
