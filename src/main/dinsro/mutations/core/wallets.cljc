(ns dinsro.mutations.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:cljs [com.fulcrologic.rad.form :as form])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   #?(:clj [dinsro.actions.core.wallets :as a.c.wallets])
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.words]
   #?(:cljs [taoensso.timbre :as log])))

(comment ::pc/_ ::m.c.wallets/_)

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
       (let [body     (get-in env [:result :body])
             response (get body `create!)]
         (log/spy :info response)
         (let [{:keys [id]}     response
               target-component (comp/registry-key->class :dinsro.ui.core.wallets/WalletForm)]
           (form/mark-all-complete! component)
           (form/view! app target-component id))
         {}))))

(defsc RollWord
  [_this _props]
  {:query [::m.words/word ::m.words/position ::m.words/id]
   :ident ::m.words/id})

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

#?(:clj (def resolvers [create! roll!]))
