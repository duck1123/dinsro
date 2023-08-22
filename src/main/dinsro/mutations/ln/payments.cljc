(ns dinsro.mutations.ln.payments
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.payments :as p.ln.payments])
   [dinsro.responses.ln.payments :as r.ln.payments]))

(def model-key ::m.ln.payments/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.payments/deleted-records]}
     (p.ln.payments/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.payments/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.payments/DeleteResponse))))

#?(:clj (def resolvers [delete!]))
