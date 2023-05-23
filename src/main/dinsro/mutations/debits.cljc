(ns dinsro.mutations.debits
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.debits :as m.debits]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.debits :as p.debits])
   [dinsro.responses.debits :as r.debits]))

(def id-key ::m.debits/id)

#?(:cljs (comment ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.debits/id}
      ::pc/output [::mu/status ::mu/errors ::r.debits/deleted-records]}
     (p.debits/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.debits/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.debits/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
