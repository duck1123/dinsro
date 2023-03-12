(ns dinsro.mutations.nostr.requests
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.requests :as a.n.requests])
   [dinsro.model.nostr.requests :as m.n.requests]))

(comment ::m.n.requests/_  ::pc/_)

#?(:clj
   (pc/defmutation start! [_env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::status ::errors]}
     (a.n.requests/do-start! props))

   :cljs
   (fm/defmutation start! [_props]
     (action    [_env] true)
     (remote    [_env]  true)))

#?(:clj
   (pc/defmutation stop! [_env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::status ::errors]}
     (a.n.requests/do-stop! props))

   :cljs
   (fm/defmutation stop! [_props]
     (action    [_env] true)
     (remote    [_env]  true)))

#?(:clj
   (def resolvers
     [start! stop!]))
