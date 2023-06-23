(ns dinsro.mutations.nostr.runs
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.runs :as p.n.runs])))

(comment ::m.n.runs/_  ::pc/_ ::mu/_)

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.runs/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.runs/item]}
     (p.n.runs/delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation stop! [_env props]
     {::pc/params #{::m.n.runs/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.runs/item]}
     (p.n.runs/stop! props))

   :cljs
   (fm/defmutation stop! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [delete! stop!]))
