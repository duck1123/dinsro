(ns dinsro.mutations.ln-payreqs
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.ln-payreqs :as a.ln-payreqs])
   [dinsro.model.ln-invoices :as m.ln-invoices]))

(comment ::m.ln-invoices/_ ::pc/_)

#?(:clj
   (pc/defmutation decode
     [_env props]
     {::pc/params #{::m.ln-invoices/payment-request}
      ::pc/output [:status]}
     (a.ln-payreqs/decode props))
   :cljs
   (fm/defmutation decode [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation submit!
     [_env props]
     {::pc/params #{::m.ln-invoices/id}
      ::pc/output [:status]}
     (a.ln-payreqs/submit! props))
   :cljs
   (fm/defmutation submit! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (def resolvers
     [decode submit!]))
