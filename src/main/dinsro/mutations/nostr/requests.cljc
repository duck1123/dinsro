(ns dinsro.mutations.nostr.requests
  (:refer-clojure :exclude [run!])
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.requests :as m.n.requests]
   #?(:clj [dinsro.processors.nostr.requests :as p.n.requests])))

;; [[../../actions/nostr/requests.clj]]

(comment ::m.n.requests/_  ::pc/_)

#?(:clj
   (pc/defmutation run! [_env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::status ::errors]}
     (p.n.requests/run! props))

   :cljs
   (fm/defmutation run! [_props]
     (action [_env] true)
     (remote [_env]  true)))

#?(:clj
   (pc/defmutation start! [_env props]
     {::pc/params #{::m.n.requests/id}
      ::pc/output [::status ::errors]}
     (p.n.requests/start! props))

   :cljs
   (fm/defmutation start! [_props]
     (action [_env] true)
     (remote [_env]  true)))

#?(:clj
   (def resolvers
     [run! start!]))
