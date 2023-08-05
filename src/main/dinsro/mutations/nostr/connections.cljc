(ns dinsro.mutations.nostr.connections
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.connections :as p.n.connections])))

;; [[../../model/nostr/connections.cljc]]
;; [[../../processors/nostr/connections.clj]]

#?(:cljs (comment ::mu/_ ::pc/_ ::m.n.connections/_))

;; Connect

#?(:clj
   (pc/defmutation connect!
     [_env props]
     {::pc/params #{::m.n.connections/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.connections/item]}
     (p.n.connections/connect! props))

   :cljs
   (fm/defmutation connect! [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Disconnect

#?(:clj
   (pc/defmutation disconnect!
     [_env props]
     {::pc/params #{::m.n.connections/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.connections/item]}
     (p.n.connections/disconnect! props))

   :cljs
   (fm/defmutation disconnect! [_props]
     (action [_env] true)
     (remote [_env] true)))

(def resolvers [connect! disconnect!])
