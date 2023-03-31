(ns dinsro.mutations.nostr.witnesses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   ;; #?(:clj [dinsro.actions.nostr.witnesses :as a.n.witnesses])
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.witnesses :as p.n.witnesses])))

(comment ::m.n.witnesses/_  ::pc/_ ::mu/_)

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.witnesses/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.witnesses/item]}
     (p.n.witnesses/do-delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action    [_env] true)
     (remote    [_env] true)))

#?(:clj
   (def resolvers [delete!]))
