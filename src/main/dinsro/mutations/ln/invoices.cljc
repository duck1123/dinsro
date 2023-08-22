(ns dinsro.mutations.ln.invoices
  (:require
   #?(:cljs [com.fulcrologic.fulcro.algorithms.normalized-state :as fns])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.ln.invoices :as p.ln.invoices])
   [dinsro.responses.ln.invoices :as r.ln.invoices]))

(def model-key ::m.ln.invoices/id)

#?(:cljs (comment ::pc/_ ::mu/_))

;; Delete

#?(:clj
   (pc/defmutation delete!
     [env props]
     {::pc/params #{model-key}
      ::pc/output [::mu/status ::r.ln.invoices/deleted-records]}
     (p.ln.invoices/delete! env props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (ok-action [{:keys [state] :as env}]
       (doseq [record (get-in env [:result :body `delete! ::r.ln.invoices/deleted-records])]
         (swap! state fns/remove-entity [model-key (model-key record)])))
     (remote [env]
       (fm/returning env r.ln.invoices/DeleteResponse))))

;; Submit

#?(:clj
   (pc/defmutation submit!
     [_env props]
     {::pc/params #{::m.ln.invoices/id}
      ::pc/output [::mu/status]}
     (comment props))
   :cljs
   (fm/defmutation submit! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete! submit!]))
