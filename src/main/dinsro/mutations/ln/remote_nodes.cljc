(ns dinsro.mutations.ln.remote-nodes
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes])
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]))

(comment ::pc/_ ::m.ln.remote-nodes/_)

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.ln.remote-nodes/keys [id]}]
     {::pc/params #{::m.ln.remote-nodes/id}
      ::pc/output [:status]}
     (a.ln.remote-nodes/fetch! id)
     {:status :ok})
   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [fetch!]))
