(ns dinsro.mocks.ui.core.wallets
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]))

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

(defn NewForm-data
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
