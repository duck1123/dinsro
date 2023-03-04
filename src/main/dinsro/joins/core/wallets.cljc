(ns dinsro.joins.core.wallets
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   #?(:clj [dinsro.queries.core.words :as q.c.words])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

(defattr index ::index :ref
  {ao/target    ::m.c.wallets/id
   ao/pc-output [{::index [::m.c.wallets/id]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} a]
     (log/info :index/starting {:query-params query-params :a a})
     (let [{node-id ::m.c.nodes/id
            user-id ::m.users/id} query-params]
       (log/info :index/parsed {:node-id node-id})
       (let [ids #?(:clj
                    (if node-id
                      (q.c.wallets/find-by-core-node node-id)
                      (if user-id
                        (q.c.wallets/find-by-user user-id)
                        (q.c.wallets/index-ids)))
                    :cljs
                    (do
                      (comment node-id user-id)
                      []))]
         {::index (m.c.wallets/idents ids)})))})

(defattr addresses ::addresses :ref
  {ao/target           ::m.c.wallet-addresses/id
   ao/pc-input         #{::m.c.wallets/id}
   ao/pc-output        [{::addresses [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env {::m.c.wallets/keys [id]}]
     (let [ids (if id #?(:clj (q.c.wallet-addresses/find-by-wallet id) :cljs []) [])]
       {::addresses (m.c.wallet-addresses/idents ids)}))
   ::report/column-EQL {::addresses [::m.c.wallet-addresses/id ::m.c.wallet-addresses/address]}})

(defattr words ::words :ref
  {ao/target           ::m.c.words/id
   ao/pc-input         #{::m.c.wallets/id}
   ao/pc-output        [{::words [::m.c.words/id]}]
   ao/pc-resolve
   (fn [_env {::m.c.wallets/keys [id]}]
     (let [ids (if id #?(:clj (q.c.words/find-by-wallet id) :cljs []) [])]
       {::words (m.c.words/idents ids)}))
   ::report/column-EQL {::words [::m.c.words/id ::m.c.words/word]}})

(def attributes [index addresses words])
