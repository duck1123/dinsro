(ns dinsro.joins.rates
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])))

;; [[../actions/rates.clj][Rate Actions]]
;; [[../model/rates.cljc][Rates Model]]
;; [[../ui/rates.cljs][Rates UI]]

(defattr index ::index :ref
  {ao/target    ::m.rates/id
   ao/pc-output [{::index [::m.rates/id]}]
   ao/pc-resolve
   (fn [_env _]
     (let [ids #?(:clj (q.rates/index-ids) :cljs [])]
       {::index (m.rates/idents ids)}))})

(def attributes [index])
