(ns dinsro.mutations.core.wallets
  (:require
   #?(:cljs [com.fulcrologic.fulcro.components :as comp])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:cljs [com.fulcrologic.rad.form :as form])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   #?(:clj [dinsro.actions.core.wallets :as a.c.wallets])
   #?(:clj [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses])
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations :as mu]
   #?(:clj [dinsro.processors.core.wallets :as p.c.wallets])
   [dinsro.responses.core.wallets :as r.c.wallets]
   #?(:clj [lambdaisland.glogc :as log])))

;; [../../processors/core/wallets.clj]

#?(:clj (comment ::r.c.wallets/_))
#?(:cljs (comment ::pc/_ ::m.c.wallets/_ ::mu/_))

#?(:clj
   (pc/defmutation create!
     [env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [::mu/status]}
     (let [user-id (a.authentication/get-user-id env)
           props   (assoc props ::m.c.wallets/user user-id)
           id      (a.c.wallets/create! props)]
       {::mu/status      :ok
        ::m.c.wallets/id id}))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.c.wallets/CreationResponse))
     (ok-action [{:keys [app component] :as env}]
       (let [body             (get-in env [:result :body])
             response         (get body `create!)
             {:keys [id]}     response
             target-component (comp/registry-key->class :dinsro.ui.core.wallets/WalletForm)]
         (form/mark-all-complete! component)
         (form/view! app target-component id)
         {}))))

#?(:clj
   (pc/defmutation roll!
     [env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [::mu/status ::m.c.wallets/item]}
     (let [user-id (a.authentication/get-user-id env)
           props   (assoc props ::m.c.wallets/user user-id)]
       (p.c.wallets/roll! props)))

   :cljs
   (fm/defmutation roll! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.c.wallets/RollResponse))))

;; derive!

#?(:clj
   (pc/defmutation derive!
     [env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [::mu/status]}
     (a.c.wallets/do-derive! env props))

   :cljs
   (fm/defmutation derive! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.c.wallets/DeriveResponse))))

#?(:clj
   (pc/defmutation calculate-addresses!
     [_env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [::mu/status]}
     (log/info :calculate-addresses!/starting {})
     (let [wallet-id (::m.c.wallets/id props)]
       (a.c.wallet-addresses/calculate-addresses! wallet-id)))

   :cljs
   (fm/defmutation calculate-addresses! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env r.c.wallets/CalculateAddressesResponse))))

#?(:clj
   (pc/defmutation delete!
     [_env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [::mu/status]}
     (a.c.wallets/do-delete! props))

   :cljs
   (fm/defmutation delete! [_props]
     (action [_env] true)
     (remote [_env] true)))

#?(:clj (def resolvers
          [calculate-addresses! create! delete! derive! roll!]))
