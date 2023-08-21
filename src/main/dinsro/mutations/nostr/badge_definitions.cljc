(ns dinsro.mutations.nostr.badge-definitions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.badge-definitions :as p.n.badge-definitions])
   [dinsro.responses.nostr.badge-definitions :as r.n.badge-definitions]))

;; [[../../processors/nostr/badge_definitions.clj]]

(def model-key ::m.n.badge-definitions/id)

#?(:cljs (comment ::mu/_ ::pc/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::mu/errors ::r.n.badge-definitions/deleted-records]}
     (p.n.badge-definitions/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.badge-definitions/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.badge-definitions/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
