(ns dinsro.mutations.core.peers
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.peers :as a.c.peers])
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.core.peers :as p.c.peers])
   [dinsro.responses.core.peers :as r.c.peers]
   #?(:clj [lambdaisland.glogc :as log])))

;; [[../../processors/core/peers.clj]]
;; [[../../responses/core/peers.cljc]]

(def model-key ::m.c.peers/id)

#(:cljs (comment ::pc/_ ::m.c.blocks/_ ::mu/_))

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{::m.c.peers/id}
      ::pc/output [::mu/status]}
     (a.c.peers/create! props))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.c.peers/id}
      ::pc/output [::mu/status ::r.c.peers/deleted-records]}
     (log/debug :delete/starting {:props props})
     (p.c.peers/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.c.peers/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [_env] true)))

#?(:clj (def resolvers [create! delete!]))
