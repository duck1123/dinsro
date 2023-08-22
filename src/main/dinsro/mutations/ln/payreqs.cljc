(ns dinsro.mutations.ln.payreqs
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.payreqs :as a.ln.payreqs])
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.payreqs :as p.ln.payreqs])
   [dinsro.responses.ln.payreqs :as r.ln.payreqs]))

(def model-key ::m.ln.payreqs/id)

#?(:cljs (comment ::m.ln.invoices/_ ::pc/_ ::mu/_))

;; Decode

#?(:clj
   (pc/defmutation decode
     [_env props]
     {::pc/params #{::m.ln.invoices/payment-request}
      ::pc/output [::mu/status]}
     (a.ln.payreqs/decode props))

   :cljs
   (fm/defmutation decode [_props]
     (action [_env] true)
     (remote [_env] true)))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.payreqs/deleted-records]}
     (p.ln.payreqs/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.payreqs/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.payreqs/DeleteResponse))))

;; Submit

#?(:clj
   (pc/defmutation submit!
     [_env props]
     {::pc/params #{::m.ln.invoices/id}
      ::pc/output [::mu/status]}
     (a.ln.payreqs/submit! props))

   :cljs
   (fm/defmutation submit! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [decode delete! submit!]))
