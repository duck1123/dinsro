(ns dinsro.mutations.nostr.badge-awards
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.badge-awards :as p.n.badge-awards])
   [dinsro.responses.nostr.badge-awards :as r.n.badge-awards]))

;; [[../../processors/nostr/badge_awards.clj]]

(def model-key ::m.n.badge-awards/id)

#?(:cljs (comment ::mu/_ ::pc/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors ::r.n.badge-awards/deleted-records]}
     (p.n.badge-awards/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.badge-awards/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.badge-awards/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
