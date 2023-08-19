(ns dinsro.mutations.nostr.runs
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.runs :as p.n.runs])
   [dinsro.responses.nostr.runs :as r.n.runs]))

;; [[../../joins/nostr/runs.cljc]]
;; [[../../processors/nostr/runs.clj]]
;; [[../../responses/nostr/runs.cljc]]

(def model-key ::m.n.runs/id)

(comment ::pc/_ ::mu/_)

#?(:clj
   (pc/defmutation delete! [env props]
     {::pc/params #{::m.n.runs/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.runs/deleted-records]}
     (p.n.runs/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.runs/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [_env] true)))

#?(:clj
   (pc/defmutation stop! [_env props]
     {::pc/params #{::m.n.runs/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.runs/item]}
     (p.n.runs/stop! props))

   :cljs
   (fm/defmutation stop! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers [delete! stop!]))
