(ns dinsro.ui.core.wallets-test
  (:require
   [dinsro.client :as client]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [lambdaisland.glogc :as log]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn Report-row
  []
  {::m.c.wallets/id         (ds/gen-key ::m.c.wallets/id)
   ::m.c.wallets/name       (ds/gen-key ::m.c.wallets/name)
   ::m.c.wallets/derivation (ds/gen-key ::m.c.wallets/derivation)
   ::m.c.wallets/key        (ds/gen-key ::m.c.wallets/key)
   ::m.c.wallets/user       {::m.users/id   (ds/gen-key ::m.users/id)
                             ::m.users/name (ds/gen-key ::m.users/name)}
   ::m.c.wallets/node       {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
                             ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)}})

(defn Report-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows (map (fn [_] (Report-row)) (range 3))
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

(defn WalletForm-node-data
  []
  {})

(defn WalletForm-data
  []
  {::m.c.wallets/id         (ds/gen-key ::m.c.wallets/id)
   ::m.c.wallets/name       (ds/gen-key ::m.c.wallets/name)
   ::m.c.wallets/derivation (ds/gen-key ::m.c.wallets/derivation)
   ::m.c.wallets/node       (WalletForm-node-data)
   ::m.c.wallets/words      []

   :com.fulcrologic.fulcro.application/active-remotes   #{}
   :com.fulcrologic.fulcro.ui-state-machines/asm-id     {}
   :com.fulcrologic.fulcro.algorithms.form-state/config {}})

(defn NewWalletForm-data
  []
  (let [idents      [[:dinsro.model.core.nodes/id (ds/gen-key ::m.c.nodes/id)]]
        nodes       (mapv
                     (fn [_]
                       {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
                        ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)})
                     (range 3))
        users       (mapv
                     (fn [_]
                       {::m.users/id   (ds/gen-key ::m.users/id)
                        ::m.users/name (ds/gen-key ::m.users/name)})
                     (range 3))
        user-ids    (mapv ::m.users/id users)
        user-idents (mapv #(m.users/ident %) user-ids)

        ;; should be a temp id
        wallet-id (ds/gen-key ::m.c.wallets/id)]
    {::m.c.wallets/id         wallet-id
     ::m.c.wallets/name       (ds/gen-key ::m.c.wallets/name)
     ::m.c.wallets/derivation (ds/gen-key ::m.c.wallets/derivation)
     ::m.c.wallets/node       (WalletForm-node-data)
     ::m.c.wallets/words      []

     :com.fulcrologic.rad.picker-options/options-cache
     {:dinsro.model.core.nodes/index
      {:cached-at    1650721944333
       :query-result idents
       :options      (mapv
                      (fn [{::m.c.nodes/keys [id name]}]
                        {:text  name
                         :value id})
                      nodes)}

      :dinsro.model.users/index
      {:cached-at    1650721944333
       :query-result user-idents
       :options      (mapv
                      (fn [{::m.users/keys [id name]}]
                        {:text  name
                         :value id})
                      users)}}

     :com.fulcrologic.fulcro.application/active-remotes #{}
     :com.fulcrologic.fulcro.ui-state-machines/asm-id   {}

     :com.fulcrologic.fulcro.algorithms.form-state/config
     {:com.fulcrologic.fulcro.algorithms.form-state/id        [::m.c.wallets/id wallet-id]
      :com.fulcrologic.fulcro.algorithms.form-state/fields    #{::m.c.wallets/name}
      :com.fulcrologic.fulcro.algorithms.form-state/complete? #{::m.c.wallets/name}
      :com.fulcrologic.fulcro.algorithms.form-state/subforms
      {:dinsro.model.core.wallets/node {}
       :dinsro.model.core.wallets/user {}}

      :com.fulcrologic.fulcro.algorithms.form-state/pristine-state {}}}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NewWalletForm
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.wallets/NewWalletForm
    ::ct.fulcro3/app
    {:client-will-mount
     (fn [app]
       (let [response (client/setup-RAD app)]
         (log/info :NewWalletForm/will-mount {:response response})
         response))

     :global-error-action (fn [env]
                            (log/info :global/error {:env env}))
     :submit-transaction!
     (fn [app tx]
       (log/info :submit-transaction!/creating {:app app
                                                :env tx}))}
    ::ct.fulcro3/initial-state NewWalletForm-data}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard Report
  {::wsm/card-width 6 ::wsm/card-height 12}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.wallets/Report
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (Report-data))}))
