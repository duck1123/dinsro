(ns dinsro.mutations.core.addresses
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.addresses :as a.c.addresses])
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.core.addresses :as p.c.addresses])
   [dinsro.responses.core.addresses :as r.c.addresses]))

(def model-key ::m.c.addresses/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{::m.c.addresses/id}
      ::pc/output [::mu/status ::mu/errors ::r.c.addresses/deleted-records]}
     (p.c.addresses/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.c.addresses/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.c.addresses/DeleteResponse))))

;; Fetch

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.c.addresses/keys [id]}]
     {::pc/params #{::m.c.addresses/id}
      ::pc/output [::mu/status]}
     (a.c.addresses/fetch! id))

   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete! fetch!]))
