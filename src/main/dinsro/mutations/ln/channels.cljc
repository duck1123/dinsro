(ns dinsro.mutations.ln.channels
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.channels :as a.ln.channels])
   [dinsro.model.ln.nodes :as m.ln.nodes]))

(comment ::pc/_ ::m.ln.nodes/_)

#?(:clj
   (pc/defmutation delete!
     [_env {::m.ln.nodes/keys [id]}]
     {::pc/params #{::m.ln.nodes/id}
      ::pc/output [:status]}
     (a.ln.channels/delete! id)
     {:status :ok})
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))
