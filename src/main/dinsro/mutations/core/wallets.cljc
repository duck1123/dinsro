(ns dinsro.mutations.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:cljs [com.fulcrologic.rad.form :as form])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   #?(:clj [dinsro.actions.core.wallets :as a.c.wallets])
   #?(:clj [dinsro.actions.core.wallet-addresses :as a.c.wallet-addresses])
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   #?(:clj [lambdaisland.glogc :as log])))

(comment ::pc/_ ::m.c.wallets/_ ::m.c.wallet-addresses/_)

(defsc CreationResponse
  [_this _props]
  {:query [:status :id]})

#?(:clj
   (pc/defmutation create!
     [env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [:status]}
     (let [user-id (a.authentication/get-user-id env)
           props   (assoc props ::m.c.wallets/user user-id)
           id      (a.c.wallets/create! props)]
       {:status :ok
        :id     id}))

   :cljs
   (fm/defmutation create! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env CreationResponse))
     (ok-action [{:keys [app component] :as env}]
       (let [body             (get-in env [:result :body])
             response         (get body `create!)
             {:keys [id]}     response
             target-component (comp/registry-key->class :dinsro.ui.core.wallets/WalletForm)]
         (form/mark-all-complete! component)
         (form/view! app target-component id)
         {}))))

(defsc RollWord
  [_this _props]
  {:query [::m.c.words/word ::m.c.words/position ::m.c.words/id]
   :ident ::m.c.words/id})

(defsc RollWallet
  [_this _props]
  {:query [::m.c.wallets/id
           ::m.c.wallets/key
           {::m.c.wallets/words (comp/get-query RollWord)}]
   :ident ::m.c.wallets/id})

(defsc RollResponse
  [_this _props]
  {:query [:status
           {:wallet (comp/get-query RollWallet)}]})

#?(:clj
   (defn do-roll!
     [props]
     (let [{::m.c.wallets/keys [id]} props
           response                (a.c.wallets/roll! props)]
       (comment id)
       {:status :ok
        :wallet response})))

#?(:clj
   (pc/defmutation roll!
     [env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [:status :wallet]}
     (let [user-id (a.authentication/get-user-id env)
           props   (assoc props ::m.c.wallets/user user-id)]
       (do-roll! props)))

   :cljs
   (fm/defmutation roll! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env RollResponse))))

;; derive!

(defsc DeriveResponse
  [_this _props]
  {:query [:status]})

#?(:clj
   (defn do-derive!
     [props]
     (log/info :do-derive!/starting {:props props})))

#?(:clj
   (pc/defmutation derive!
     [env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [:status]}

     (let [user-id (a.authentication/get-user-id env)
           props   (assoc props ::m.c.wallets/user user-id)]
       (do-derive! props)))

   :cljs
   (fm/defmutation derive! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env DeriveResponse))))

(defsc CalculateAddressesResponse
  [_this _props]
  {:query [:status]})

#?(:clj
   (pc/defmutation calculate-addresses!
     [_env props]
     {::pc/params #{::m.c.wallets/id}
      ::pc/output [:status]}
     (log/info :calculate-addresses!/starting {})
     (let [wallet-id (::m.c.wallets/id props)]
       (a.c.wallet-addresses/calculate-addresses! wallet-id)))

   :cljs
   (fm/defmutation calculate-addresses! [_props]
     (action [_env] true)
     (remote [env] (fm/returning env CalculateAddressesResponse))))

#?(:clj (def resolvers [calculate-addresses!
                        create! derive! roll!]))
