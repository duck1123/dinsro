(ns dinsro.mutations.nostr.filters
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.filters :as a.n.filters])
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.mutations :as mu]))

#?(:cljs (comment ::m.n.filters/_ ::mu/_ ::pc/_))

#?(:clj
   (pc/defmutation add-filter! [_env props]
     {::pc/params #{::m.n.filters/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.filters/item]}
     (a.n.filters/do-add-filters! props))

   :cljs
   (fm/defmutation add-filter! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.filters/id}
      ::pc/output [::mu/status ::mu/errors ::m.n.filters/item]}
     (a.n.filters/do-delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [add-filter! delete!]))
