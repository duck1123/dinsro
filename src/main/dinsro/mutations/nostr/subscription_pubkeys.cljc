(ns dinsro.mutations.nostr.subscription-pubkeys
  (:require
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.nostr.relays :as m.n.relays]))

(comment ::pc/_ ::m.n.relays/_)

#?(:clj
   (pc/defmutation delete!
     [_env _props]
     {::pc/params #{::m.n.relays/id}
      ::pc/output [::status
                   ::errors
                   ::m.n.relays/item]})

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers [delete!]))
