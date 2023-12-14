(ns dinsro.mocks.ui.core.wallets
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.options.core.nodes :as o.c.nodes]
   [dinsro.options.core.wallets :as o.c.wallets]
   [dinsro.options.users :as o.users]
   [dinsro.specs :as ds]))

(defn Report-row
  []
  {o.c.wallets/id         (ds/gen-key o.c.wallets/id)
   o.c.wallets/name       (ds/gen-key o.c.wallets/name)
   o.c.wallets/derivation (ds/gen-key o.c.wallets/derivation)
   o.c.wallets/key        (ds/gen-key o.c.wallets/key)
   o.c.wallets/user       {::m.users/id   (ds/gen-key o.users/id)
                           ::m.users/name (ds/gen-key o.users/name)}})

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
  (let [idents      [[o.c.nodes/id (ds/gen-key o.c.nodes/id)]]
        nodes       (mapv
                     (fn [_]
                       {o.c.nodes/id   (ds/gen-key o.c.nodes/id)
                        o.c.nodes/name (ds/gen-key o.c.nodes/name)})
                     (range 3))
        users       (mapv
                     (fn [_]
                       {o.users/id   (ds/gen-key o.users/id)
                        o.users/name (ds/gen-key o.users/name)})
                     (range 3))
        user-ids    (mapv o.users/id users)
        user-idents (mapv #(m.users/ident %) user-ids)
        ;; should be a temp id
        wallet-id   (ds/gen-key ::m.c.wallets/id)]
    {o.c.wallets/id         wallet-id
     o.c.wallets/name       (ds/gen-key o.c.wallets/name)
     o.c.wallets/derivation (ds/gen-key ::m.c.wallets/derivation)
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

     ::fs/config
     {::fs/id             [::m.c.wallets/id wallet-id]
      ::fs/fields         #{o.c.wallets/name}
      ::fs/complete?      #{o.c.wallets/name}
      ::fs/subforms       {o.c.wallets/user {}}
      ::fs/pristine-state {}}}))
