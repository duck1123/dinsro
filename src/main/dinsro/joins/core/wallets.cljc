(ns dinsro.joins.core.wallets
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.words :as m.c.words]
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   #?(:clj [dinsro.queries.core.words :as q.c.words])
   [dinsro.specs]))

(def model-key ::m.c.wallets/id)

(def join-info
  (merge
   {:idents m.c.wallets/idents}
   #?(:clj {:indexer q.c.wallets/index-ids
            :counter q.c.wallets/count-ids})))

(defattr admin-flat-index ::admin-flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::admin-flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-flat-index (:results (j/make-admin-indexer join-info env props))})})

(defattr admin-index ::admin-index :ref
  {ao/target    ::m.c.wallets/id
   ao/pc-output [{::admin-index [:total {:results [::m.c.wallets/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr flat-index ::flat-index :ref
  {ao/target    model-key
   ao/pc-output [{::flat-index [model-key]}]
   ao/pc-resolve
   (fn [env props]
     {::flat-index (j/make-flat-indexer join-info env props)})})

(defattr index ::index :ref
  {ao/target    ::m.c.wallets/id
   ao/pc-output [{::index [:total {:results [::m.c.wallets/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

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

(def attributes [admin-flat-index admin-index flat-index index addresses words])
