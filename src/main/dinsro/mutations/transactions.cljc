(ns dinsro.mutations.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.transactions :as p.transactions])
   [dinsro.responses.transactions :as r.transactions]))

(def id-key ::m.transactions/id)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.transactions/id}
      ::pc/output [::mu/status ::mu/errors ::r.transactions/deleted-records]}
     (p.transactions/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.transactions/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.transactions/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
