(ns dinsro.mutations.ln.invoices
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.mutations :as mu]))

#?(:cljs (comment ::m.ln.invoices/_ ::pc/_ ::mu/_))

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

#?(:clj (def resolvers [submit!]))
