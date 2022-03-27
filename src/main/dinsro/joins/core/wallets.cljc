(ns dinsro.joins.core.wallets
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.model.core.wallet-addresses :as m.c.wallet-addresses]
   [dinsro.model.core.words :as m.c.words]
   #?(:clj [dinsro.queries.core.wallets :as q.c.wallets])
   #?(:clj [dinsro.queries.core.wallet-addresses :as q.c.wallet-addresses])
   #?(:clj [dinsro.queries.core.words :as q.c.words])
   [dinsro.specs]))

(defattr index ::m.c.wallets/index :ref
  {ao/target    ::m.c.wallets/id
   ao/pc-output [{::m.c.wallets/index [::m.c.wallets/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.c.wallets/index-ids) :cljs [])]
       {::m.c.wallets/index (m.c.wallets/idents ids)}))})

(defattr addresses ::m.c.wallets/addresses :ref
  {ao/target           ::m.c.wallet-addresses/id
   ao/pc-input         #{::m.c.wallets/id}
   ao/pc-output        [{::m.c.wallets/addresses [::m.c.wallet-addresses/id]}]
   ao/pc-resolve
   (fn [_env {::m.c.wallets/keys [id]}]
     (let [ids (if id #?(:clj (q.c.wallet-addresses/find-by-wallet id) :cljs []) [])]
       {::m.c.wallets/addresses (m.c.wallet-addresses/idents ids)}))
   ::report/column-EQL {::m.c.wallets/addresses [::m.c.wallet-addresses/id ::m.c.wallet-addresses/address]}})

(defattr words ::m.c.wallets/words :ref
  {ao/target           ::m.c.words/id
   ao/pc-input         #{::m.c.wallets/id}
   ao/pc-output        [{::m.c.wallets/words [::m.c.words/id]}]
   ao/pc-resolve
   (fn [_env {::m.c.wallets/keys [id]}]
     (let [ids (if id #?(:clj (q.c.words/find-by-wallet id) :cljs []) [])]
       {::m.c.wallets/words (m.c.words/idents ids)}))
   ::report/column-EQL {::m.c.wallets/words [::m.c.words/id ::m.c.words/word
                                           ;; ::m.c.words/position
                                             ]}})

(def attributes [index addresses words])
