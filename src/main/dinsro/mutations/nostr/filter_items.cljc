(ns dinsro.mutations.nostr.filter-items
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.nostr.filter-items :as p.n.filter-items])
   [dinsro.resources.nostr.filter-items :as r.n.filter-items]))

(def model-key ::m.n.filter-items/id)

#?(:cljs (comment ::mu/_ ::pc/_ ::m.n.filter-items/_))

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.filter-items/id}
      ::pc/output [::mu/status ::mu/errors ::r.n.filter-items/deleted-records]}
     (p.n.filter-items/delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.n.filter-items/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.n.filter-items/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
