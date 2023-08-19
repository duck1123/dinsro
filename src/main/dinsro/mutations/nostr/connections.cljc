(ns dinsro.mutations.nostr.connections
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.connections :as p.n.connections])
   [dinsro.responses.nostr.connections :as r.n.connections]))

;; [[../../model/nostr/connections.cljc]]
;; [[../../processors/nostr/connections.clj]]
;; [[../../responses/nostr/connections.cljc]]
;; [[../../ui/admin/nostr/connections.cljc]]

(def model-key ::m.n.connections/id)

#?(:cljs (comment ::mu/_ ::pc/_  ::r.n.connections/_))

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

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.n.connections/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.connections/deleted-records]}
     (p.n.connections/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.connections/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.connections/DeleteResponse))))

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

(def resolvers [connect! delete! disconnect!])
