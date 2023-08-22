(ns dinsro.mutations.ln.remote-nodes
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes])
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.remote-nodes :as p.ln.remote-nodes])
   [dinsro.responses.ln.remote-nodes :as r.ln.remote-nodes]))

(def model-key ::m.ln.remote-nodes/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.remote-nodes/deleted-records]}
     (p.ln.remote-nodes/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.remote-nodes/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.remote-nodes/DeleteResponse))))

#?(:clj
   (pc/defmutation fetch!
     [_env {::m.ln.remote-nodes/keys [id]}]
     {::pc/params #{model-key}
      ::pc/output [::mu/status]}
     (a.ln.remote-nodes/fetch! id)
     {::mu/status :ok})
   :cljs
   (fm/defmutation fetch! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [delete! fetch!]))
