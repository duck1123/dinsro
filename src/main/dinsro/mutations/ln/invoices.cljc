(ns dinsro.mutations.ln.invoices
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln.invoices :as a.ln-invoices])
   [dinsro.model.ln.invoices :as m.ln-invoices]))

(comment ::m.ln-invoices/_ ::pc/_)

#?(:clj
   (pc/defmutation submit!
     [_env props]
     {::pc/params #{::m.ln-invoices/id}
      ::pc/output [:status]}
     (a.ln-invoices/add-invoice props))
   :cljs
   (fm/defmutation submit! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [submit!]))
