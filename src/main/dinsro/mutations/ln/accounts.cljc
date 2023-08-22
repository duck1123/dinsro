(ns dinsro.mutations.ln.accounts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.accounts :as p.ln.accounts])
   [dinsro.responses.ln.accounts :as r.ln.accounts]))

(def model-key ::m.ln.accounts/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.accounts/deleted-records]}
     (p.ln.accounts/delete! env props))
   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.accounts/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.accounts/DeleteResponse))))

#?(:clj
   (def resolvers [delete!]))
