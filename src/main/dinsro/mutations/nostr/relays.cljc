(ns dinsro.mutations.nostr.relays
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.subscription-pubkeys :as a.n.subscription-pubkeys])
   #?(:cljs [dinsro.handlers.nostr.relays :as h.n.relays])
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.connections :as p.n.connections])
   #?(:clj [dinsro.processors.nostr.relays :as p.n.relays])
   #?(:cljs [dinsro.responses.nostr.relays :as r.n.relays])))

;; [[../../actions/nostr/relays.clj]]
;; [[../../joins/nostr/relays.cljc]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../queries/nostr/relays.clj]]
;; [[../../ui/nostr/event_tags/relays.cljs]]
;; [[../../ui/nostr/relays.cljs]]

#?(:cljs (comment ::pc/_ ::m.n.relays/_ ::mu/_))

#?(:clj
   (pc/defmutation fetch!
     [env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.relays/item]}
     (a.n.subscription-pubkeys/do-fetch! env props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning r.n.relays/FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::FetchReponse]))))))

#?(:clj
   (pc/defmutation fetch-events!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors]}
     (p.n.relays/fetch-events! props))

   :cljs
   (fm/defmutation fetch-events! [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Connect

#?(:clj
   (pc/defmutation connect!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.relays/item]}
     (p.n.relays/connect! props))

   :cljs
   (fm/defmutation connect! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env r.n.relays/ConnectResponse))
     (ok-action [env]  (h.n.relays/handle-connect env))))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.relays/item]}
     (p.n.relays/delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env]  true)))

;; Toggle

#?(:clj
   (pc/defmutation toggle!
     [env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.relays/item]}
     (p.n.connections/toggle! env props))

   :cljs
   (fm/defmutation toggle! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning r.n.relays/ConnectResponse)
           (fm/with-target (targeting/append-to [:responses/id ::ConnectReponse]))))))

;; Submit

#?(:clj
   (pc/defmutation submit!
     [_env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.relays/item]}
     (p.n.relays/submit! props))

   :cljs
   (fm/defmutation submit! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning r.n.relays/ConnectResponse)
           (fm/with-target (targeting/append-to [:responses/id ::SubmitReponse]))))))

#?(:clj (def resolvers [connect! delete! fetch! fetch-events! submit! toggle!]))
