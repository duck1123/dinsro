(ns dinsro.mutations.nostr.badge-acceptances
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.badge-acceptances :as p.n.badge-acceptances])))

;; [../../processors/nostr/badge_acceptances.clj]

#?(:cljs (comment ::mu/_ ::pc/_
                  ::m.n.relays/id ::m.n.pubkeys/id
                  ::m.n.badge-acceptances/id))

;; fetch!

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.n.relays/id ::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.badge-acceptances/item]}
     (p.n.badge-acceptances/fetch! props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [fetch!]))
