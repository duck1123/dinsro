(ns dinsro.mutations.core.addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.addresses :as a.c.addresses])
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.mutations :as mu]))

#?(:cljs (comment ::pc/_ ::m.c.addresses/_ ::mu/_))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.c.addresses/id}
      ::pc/output [::mu/status]}
     (a.c.addresses/do-delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.c.addresses/keys [id]}]
     {::pc/params #{::m.c.addresses/id}
      ::pc/output [::mu/status]}
     (a.c.addresses/fetch! id))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete! fetch!]))
