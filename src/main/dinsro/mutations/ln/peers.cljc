(ns dinsro.mutations.ln.peers
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.peers :as a.ln.peers])
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.peers :as p.ln.peers])
   [dinsro.responses.ln.peers :as r.ln.peers]
   #?(:clj [lambdaisland.glogc :as log])))

(def model-key ::m.ln.peers/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Create

#?(:clj
   (pc/defmutation create!
     [_env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status]}
     (log/info :create!/starting {:props props})
     (a.ln.peers/create! props))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.peers/deleted-records]}
     (log/debug :delete!/starting {:props props})
     (p.ln.peers/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.peers/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.peers/DeleteResponse))))

#?(:clj (def resolvers [create! delete!]))
