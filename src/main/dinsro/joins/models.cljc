(ns dinsro.joins.models
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   ;; [dinsro.joins :as j]
   ;; [dinsro.model.accounts :as m.accounts]
   ;; [dinsro.model.core.wallets :as m.c.wallets]
   ;; [dinsro.model.debits :as m.debits]
   [dinsro.model.models :as m.models :refer [defmodel]]
   ;; [dinsro.model.rate-sources :as m.rate-sources]
   ;; [dinsro.model.transactions :as m.transactions]
   ;; #?(:clj [dinsro.queries.accounts :as q.accounts])
   ;; #?(:clj [dinsro.queries.debits :as q.debits])
   ;; #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]))

;; [[../model/models.cljc]]

(def model-key ::m.models/id)

(defattr index ::index :ref
  {ao/target    model-key
   ao/pc-output [{::index [:total {:results [model-key]}]}]
   ao/pc-resolve
   (fn [_env _props]
     (let [models []]
       {::index
        {:total (count models)
         :results models}}))})

(def attributes [index])

(defmodel model-key
  {::m.models/name "Accounts"})
