(ns dinsro.joins.rates
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.rates :as q.rates])))

;; [[../actions/rates.clj][Rate Actions]]
;; [[../model/rates.cljc][Rates Model]]
;; [[../ui/rates.cljs][Rates UI]]

(comment ::m.rate-sources/_)

(defattr index ::index :ref
  {ao/target    ::m.rates/id
   ao/pc-output [{::index [:total {:results [::m.rates/id]}]}]
   ao/pc-resolve
   (fn [{:keys [query-params]} _]
     (let [ids #?(:clj (q.rates/index-ids query-params)
                  :cljs (do (comment query-params) []))]
       {::index {:total   #?(:clj (q.rates/count-ids query-params) :cljs 0)
                 :results (m.rates/idents ids)}}))})

(def attributes [index])
