(ns dinsro.mutations.nostr.event-tags
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.event-tags :as p.n.event-tags])
   #?(:cljs [dinsro.responses.nostr.event-tags :as r.n.event-tags])))

#?(:cljs (comment ::m.n.event-tags/id ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation fetch!
     [env props]
     {::pc/params #{::m.n.event-tags/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.event-tags/item]}
     (p.n.event-tags/fetch! env props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action    [_env] true)
     (remote    [env]
       (-> env
           (fm/returning r.n.event-tags/FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::FetchReponse]))))))

#?(:clj (def resolvers [fetch!]))
