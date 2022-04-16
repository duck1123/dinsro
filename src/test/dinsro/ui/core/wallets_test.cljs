(ns dinsro.ui.core.wallets-test
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.users :as m.users]
   [dinsro.client :as client]
   [dinsro.specs :as ds]
   [dinsro.ui.core.wallets :as u.c.wallets]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(defn WalletReport-row
  []
  {::m.c.wallets/id   (ds/gen-key ::m.c.wallets/id)
   ::m.c.wallets/name (ds/gen-key ::m.c.wallets/name)
   ::m.c.wallets/user {::m.users/id   (ds/gen-key ::m.users/id)
                       ::m.users/name (ds/gen-key ::m.users/name)}
   ::m.c.wallets/node {::m.c.nodes/id   (ds/gen-key ::m.c.nodes/id)
                       ::m.c.nodes/name (ds/gen-key ::m.c.nodes/name)}})

(defn WalletReport-data
  []
  {:ui/busy?        false
   :ui/cache        {}
   :ui/controls     []
   :ui/current-page 1
   :ui/current-rows [(WalletReport-row)]
   :ui/loaded-data  []
   :ui/page-count   1
   :ui/parameters   {}})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard WalletReport
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.c.wallets/WalletReport
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (WalletReport-data))}))
