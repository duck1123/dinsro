(ns dinsro.mutations.nostr.event-tags
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting])
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.event-tags :as p.n.event-tags])
   [dinsro.responses.nostr.event-tags :as r.n.event-tags]))

;; [[../../actions/nostr/event_tags.clj]]
;; [[../../model/nostr/event_tags.cljc]]
;; [[../../processors/nostr/event_tags.clj]]
;; [[../../queries/nostr/event_tags.clj]]
;; [[../../responses/nostr/event_tags.cljc]]

(def model-key ::m.n.event-tags/id)

#?(:cljs (comment ::mu/_ ::pc/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors ::r.n.event-tags/deleted-records]}
     (p.n.event-tags/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.event-tags/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.event-tags/DeleteResponse))))

;; fetch!

#?(:clj
   (pc/defmutation fetch!
     [env props]
     {::pc/params #{::m.n.event-tags/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.event-tags/item]}
     (p.n.event-tags/fetch! env props))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (-> env
           (fm/returning r.n.event-tags/FetchResponse)
           (fm/with-target (targeting/append-to [:responses/id ::FetchReponse]))))))

#?(:clj (def resolvers [delete! fetch!]))
