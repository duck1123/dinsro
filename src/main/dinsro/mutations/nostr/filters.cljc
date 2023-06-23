(ns dinsro.mutations.nostr.filters
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.nostr.filters :as a.n.filters])
   [dinsro.model.nostr.filters :as m.n.filters]))

#?(:cljs (comment ::pc/_ ::m.n.filters/_))

#?(:clj
   (pc/defmutation add-filter! [_env props]
     {::pc/params #{::m.n.filters/id}
      ::pc/output [::status ::errors ::m.n.filters/item]}
     (a.n.filters/do-add-filters! props))

   :cljs
   (fm/defmutation add-filter! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.filters/id}
      ::pc/output [::status ::errors ::m.n.filters/item]}
     (a.n.filters/do-delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [add-filter! delete!]))
