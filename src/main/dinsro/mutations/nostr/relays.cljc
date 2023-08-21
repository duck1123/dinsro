(ns dinsro.mutations.nostr.relays
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:cljs [dinsro.handlers.nostr.relays :as h.n.relays])
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.connections :as p.n.connections])
   #?(:clj [dinsro.processors.nostr.relays :as p.n.relays])
   [dinsro.responses.nostr.relays :as r.n.relays]))

;; [[../../actions/nostr/relays.clj]]
;; [[../../joins/nostr/relays.cljc]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../queries/nostr/relays.clj]]
;; [[../../responses/nostr/relays.cljc]]
;; [[../../ui/nostr/event_tags/relays.cljs]]
;; [[../../ui/nostr/relays.cljs]]

(def model-key ::m.n.relays/id)

#?(:cljs (comment ::pc/_ ::mu/_))

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

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.relays/deleted-records]}
     (p.n.relays/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.relays/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.relays/DeleteResponse))))

;; Fetch Events

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

#?(:clj (def resolvers [connect! delete! fetch-events! submit! toggle!]))
