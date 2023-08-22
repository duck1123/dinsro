(ns dinsro.mutations.core.connections
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.connections :as a.c.connections])
   [dinsro.model.core.connections :as m.c.connections]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.core.connections :as p.c.connections])
   [dinsro.responses.core.connections :as r.c.connections]))

(def model-key ::m.c.connections/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Create

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{::m.c.connections/id}
      ::pc/output [::mu/status]}
     (a.c.connections/create! props))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.c.connections/id}
      ::pc/output [::mu/status ::mu/errors ::r.c.connections/deleted-records]}
     (p.c.connections/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.c.connections/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.c.connections/DeleteResponse))))

#?(:clj (def resolvers [create! delete!]))
