(ns dinsro.mutations.nostr.badge-acceptances
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.badge-acceptances :as p.n.badge-acceptances])
   [dinsro.responses.nostr.badge-acceptances :as r.n.badge-acceptances]))

;; [[../../processors/nostr/badge_acceptances.clj]]

(def model-key ::m.n.badge-acceptances/id)

#?(:cljs (comment ::mu/_ ::pc/_
                  ::m.n.relays/id ::m.n.pubkeys/id))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors ::r.n.badge-acceptances/deleted-records]}
     (p.n.badge-acceptances/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.badge-acceptances/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.badge-acceptances/DeleteResponse))))

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

#?(:clj (def resolvers [delete! fetch!]))
