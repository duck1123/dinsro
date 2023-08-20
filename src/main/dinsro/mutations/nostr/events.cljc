(ns dinsro.mutations.nostr.events
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.events :as p.n.events])
   [dinsro.responses.nostr.events :as r.n.events]
   #?(:clj [lambdaisland.glogc :as log])))

;; [[../../actions/nostr/events.clj]]
;; [[../../processors/nostr/events.clj]]
;; [[../../responses/nostr/events.cljc]]
;; [[../../ui/admin/nostr/events.cljc]]

(def model-key ::m.n.pubkeys/id)

#?(:cljs (comment ::m.n.events/_ ::m.n.relays/_ ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors ::r.n.events/deleted-records]}
     (p.n.events/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.events/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.events/DeleteResponse))))

;; Fetch

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.n.events/id}
      ::pc/output [::mu/status
                   ::mu/errors
                   ::m.n.events/item]}
     (try
       (let [updated-item (p.n.events/fetch! props)]
         {::mu/status       :ok
          ::m.n.events/item updated-item})
       (catch Exception ex
         (log/error :fetch!/errored {:ex ex}))))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning r.n.events/FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::r.n.events/FetchReponse]))))))

;; Fetch Events

#?(:clj
   (pc/defmutation fetch-events!
     [_env props]
     {::pc/params #{::m.n.relays/id ::m.n.pubkeys/id}
      ::pc/output [::mu/status ::mu/errors]}
     (p.n.events/fetch-events! props))

   :cljs
   (fm/defmutation fetch-events! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete! fetch! fetch-events!]))
