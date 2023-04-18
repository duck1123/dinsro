(ns dinsro.mutations.ln.remote-nodes
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes])
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.mutations :as mu]))

#?(:cljs (comment ::pc/_ ::m.ln.remote-nodes/_ ::mu/_))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.ln.remote-nodes/id}
      ::pc/output [::mu/status]}
     (a.ln.remote-nodes/do-delete! props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.ln.remote-nodes/keys [id]}]
     {::pc/params #{::m.ln.remote-nodes/id}
      ::pc/output [::mu/status]}
     (a.ln.remote-nodes/fetch! id)
     {::mu/status :ok})
   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [delete! fetch!]))
