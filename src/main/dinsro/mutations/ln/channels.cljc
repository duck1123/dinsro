(ns dinsro.mutations.ln.channels
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.channels :as p.ln.channels])
   [dinsro.responses.ln.channels :as r.ln.channels]))

(def model-key ::m.ln.channels/id)

(comment ::pc/_ ::m.ln.nodes/_ ::mu/_)

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.channels/deleted-records]}
     (p.ln.channels/delete! env props))
   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.channels/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.channels/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
