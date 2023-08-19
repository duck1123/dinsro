(ns dinsro.mutations.nostr.witnesses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.witnesses :as p.n.witnesses])
   [dinsro.responses.nostr.witnesses :as r.n.witnesses]))

;; [[../../model/nostr/witnesses.cljc]]
;; [[../../processors/nostr/witnesses.clj]]
;; [[../../responses/nostr/witnesses.cljc]]

(def model-key ::m.n.witnesses/id)

(comment ::pc/_ ::mu/_)

#?(:clj
   (pc/defmutation delete! [env props]
     {::pc/params #{::m.n.witnesses/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.witnesses/deleted-records]}
     (p.n.witnesses/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.witnesses/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.witnesses/DeleteResponse))))

#?(:clj
   (def resolvers [delete!]))
