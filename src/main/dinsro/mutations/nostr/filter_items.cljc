(ns dinsro.mutations.nostr.filter-items
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   #?(:clj [dinsro.processors.nostr.filter-items :as p.n.filter-items])))

#?(:cljs (comment ::pc/_ ::m.n.filter-items/_))

#?(:clj
   (pc/defmutation delete! [_env props]
     {::pc/params #{::m.n.filter-items/id}
      ::pc/output [::status ::errors ::m.n.filter-items/item]}
     (p.n.filter-items/delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete!]))
