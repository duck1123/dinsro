(ns dinsro.mutations.user-pubkeys
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.user-pubkeys :as m.user-pubkeys]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.user-pubkeys :as p.user-pubkeys])
   [dinsro.responses.user-pubkeys :as r.user-pubkeys]))

(def id-key ::m.user-pubkeys/id)

#?(:cljs (comment ::pc/_ ::mu/_ ::r.user-pubkeys/_))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.user-pubkeys/id}
      ::pc/output [::mu/status ::mu/errors ::r.user-pubkeys/deleted-records]}
     (p.user-pubkeys/delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.user-pubkeys/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.user-pubkeys/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
