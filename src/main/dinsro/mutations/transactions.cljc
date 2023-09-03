(ns dinsro.mutations.transactions
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.mutations :as mu]
   [dinsro.options.transactions :as o.transactions]
   #?(:clj [dinsro.processors.transactions :as p.transactions])
   [dinsro.responses.transactions :as r.transactions]))

(def model-key o.transactions/id)

#?(:cljs (comment ::mu/_ ::pc/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{o.transactions/id}
      ::pc/output [mu/status mu/errors ::r.transactions/deleted-records]}
     (p.transactions/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.transactions/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.transactions/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
