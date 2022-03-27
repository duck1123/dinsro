(ns dinsro.joins.core.wallets
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.wallets :as m.wallets]
   [dinsro.model.core.wallet-addresses :as m.wallet-addresses]
   [dinsro.model.core.words :as m.words]
   #?(:clj [dinsro.queries.core.wallets :as q.wallets])
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.wallet-addresses])
   #?(:clj [dinsro.queries.core.words :as q.words])
   [dinsro.specs]))

(defattr index ::m.wallets/index :ref
  {ao/target    ::m.wallets/id
   ao/pc-output [{::m.wallets/index [::m.wallets/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.wallets/index-ids) :cljs [])]
       {::m.wallets/index (m.wallets/idents ids)}))})

(defattr addresses ::m.wallets/addresses :ref
  {ao/target           ::m.wallet-addresses/id
   ao/pc-input         #{::m.wallets/id}
   ao/pc-output        [{::m.wallets/addresses [::m.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env {::m.wallets/keys [id]}]
     (let [ids (if id #?(:clj (q.wallet-addresses/find-by-wallet id) :cljs []) [])]
       {::m.wallets/addresses (m.wallet-addresses/idents ids)}))
   ::report/column-EQL {::m.wallets/addresses [::m.wallet-addresses/id ::m.wallet-addresses/address]}})

(defattr words ::m.wallets/words :ref
  {ao/target           ::m.words/id
   ao/pc-input         #{::m.wallets/id}
   ao/pc-output        [{::m.wallets/words [::m.words/id]}]
   ao/pc-resolve
   (fn [_env {::m.wallets/keys [id]}]
     (let [ids (if id #?(:clj (q.words/find-by-wallet id) :cljs []) [])]
       {::m.wallets/words (m.words/idents ids)}))
   ::report/column-EQL {::m.wallets/words [::m.words/id ::m.words/word
                                           ;; ::m.words/position
                                           ]}})

(def attributes [index addresses words])
