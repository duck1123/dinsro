(ns dinsro.mutations.users
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.users :as p.users])
   [dinsro.responses.users :as r.users]))

(def id-key ::m.users/id)

#?(:cljs (comment ::pc/_ ::mu/_ ::r.users/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.users/id}
      ::pc/output [::mu/status ::mu/errors ::r.users/deleted-records]}
     (p.users/delete! env props))

   :cljs
   (defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.users/deleted-records])]
         (swap! state fns/remove-entity [id-key (id-key record)])))
     (remote [env]
       (fm/returning env r.users/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
