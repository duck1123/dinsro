(ns dinsro.mutations.ln.accounts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.accounts :as a.ln.accounts])
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.mutations :as mu]))

#?(:cljs (comment ::pc/_ ::m.ln.accounts/_ ::mu/_))

#?(:clj
   (pc/defmutation delete!
     [_env {::m.ln.accounts/keys [id]}]
     {::pc/params #{::m.ln.accounts/id}
      ::pc/output [::mu/status]}
     (a.ln.accounts/delete! id)
     {:status :ok})
   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [delete!]))
