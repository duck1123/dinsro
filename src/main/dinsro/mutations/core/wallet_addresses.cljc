(ns dinsro.mutations.core.wallet-addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses])
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   [lambdaisland.glogc :as log]))

#?(:cljs (comment ::m.c.wallet-addresses/_ ::pc/_ ::log/_ ::mu/_))

#?(:clj
   (pc/defmutation generate!
     [_env {::m.c.wallet-addresses/keys [id]}]
     {::pc/params #{::m.c.wallet-addresses/id}
      ::pc/output [::mu/status]}
     (log/info :generate!/starting {:id id})
     (let [node (q.c.wallet-addresses/read-record id)]
       (a.c.wallet-addresses/generate! node)
       {::mu/status :ok}))

   :cljs
   (defmutation generate! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [generate!]))
