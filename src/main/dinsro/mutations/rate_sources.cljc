(ns dinsro.mutations.rate-sources
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.rate-sources :as a.rate-sources])
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations :as mu]))

#?(:cljs (comment ::m.rate-sources/_ ::mu/_ ::pc/_))

#?(:clj
   (defn do-run!
     [id]
     (a.rate-sources/run-query! id)
     {::mu/status :success}))

#?(:clj
   (pc/defmutation run-query!
     [_request {::m.rate-sources/keys [id]}]
     {::pc/params #{::m.rate-sources/id}
      ::pc/output [::mu/status]}
     (do-run! id))
   :cljs
   (fm/defmutation run-query! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [run-query!]))
