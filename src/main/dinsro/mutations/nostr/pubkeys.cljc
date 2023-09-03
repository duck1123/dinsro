(ns dinsro.mutations.nostr.pubkeys
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def =>]]
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.pubkey-contacts :as a.n.pubkey-contacts])
   #?(:clj [dinsro.actions.nostr.pubkey-events :as a.n.pubkey-events])
   #?(:clj [dinsro.actions.nostr.pubkeys :as a.n.pubkeys])
   #?(:cljs [dinsro.handlers.nostr.pubkeys :as h.n.pubkeys])
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.badge-awards :as p.n.badge-awards])
   #?(:clj [dinsro.processors.nostr.badge-definitions :as p.n.badge-definitions])
   #?(:clj [dinsro.processors.nostr.pubkeys :as p.n.pubkeys])
   [dinsro.responses.nostr.pubkeys :as r.n.pubkeys]))

;; [[../../actions/nostr/pubkeys.clj]]
;; [[../../model/nostr/relays.cljc]]
;; [[../../model/nostr/relay_pubkeys.cljc]]
;; [[../../processors/nostr/pubkeys.clj]]
;; [[../../responses/nostr/pubkeys.cljc]]
;; [[../../ui/nostr/pubkeys.cljs]]

(def model-key ::m.n.pubkeys/id)

#?(:cljs (comment ::m.contacts/_ ::m.n.badge-awards/id ::m.n.relays/_ ::pc/_))

(>def ::item ::m.n.pubkeys/item)
(>def ::creation-response (s/keys :req [::mu/status ::mu/errors ::m.n.pubkeys/item]))

;; Add Contact

#?(:clj
   (pc/defmutation add-contact!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::m.contacts/item]}
     (a.n.pubkeys/add-contact! props))

   :cljs
   (fm/defmutation add-contact! [_props]
     (action [_env] true)
     (remote [env]  (fm/returning env r.n.pubkeys/AddContactResponse))))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.pubkeys/deleted-records]}
     (p.n.pubkeys/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.pubkeys/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.pubkeys/DeleteResponse))))

;; Fetch

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id ::m.n.relays/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.pubkeys/item]}
     (p.n.pubkeys/fetch! props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env r.n.pubkeys/FetchResponse))
     (ok-action [env]  (h.n.pubkeys/handle-fetch env))))

;; Fetch Awards

#?(:clj
   (pc/defmutation fetch-awards!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.badge-awards/items]}
     (p.n.badge-awards/fetch! props))

   :cljs
   (fm/defmutation fetch-awards! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env r.n.pubkeys/FetchAwardsResponse))
     (ok-action [env]  (h.n.pubkeys/handle-fetch env))))

;; Fetch Contacts

#?(:clj
   (pc/defmutation fetch-contacts!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.pubkeys/item]}
     (a.n.pubkey-contacts/do-fetch-contacts! props))

   :cljs
   (fm/defmutation fetch-contacts! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env r.n.pubkeys/FetchContactsResponse))
     (ok-action [env]  (h.n.pubkeys/handle-fetch-contacts env))))

;; Fetch Definitions

#?(:clj
   (pc/defmutation fetch-definitions!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.pubkeys/item]}
     (p.n.badge-definitions/do-fetch-definitions! props))

   :cljs
   (fm/defmutation fetch-definitions! [_props]
     (action [_env] true)
     (remote [env]  (fm/returning env r.n.pubkeys/FetchDefinitionsResponse))))

;; Fetch Events

#?(:clj
   (pc/defmutation fetch-events!
     [_env props]
     {::pc/params #{::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.pubkeys/item]}
     (a.n.pubkey-events/do-fetch-events! props))

   :cljs
   (fm/defmutation fetch-events! [_props]
     (action    [_env] true)
     (remote    [env]  (fm/returning env r.n.pubkeys/FetchEventsResponse))
     (ok-action [env]  (h.n.pubkeys/handle-fetch-events env))))

#?(:clj
   (def resolvers
     [add-contact!
      delete!
      fetch!
      fetch-awards!
      fetch-contacts!
      fetch-definitions!
      fetch-events!]))
