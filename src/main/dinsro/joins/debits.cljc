(ns dinsro.joins.debits
  (:require
   [com.fulcrologic.guardrails.core :refer [=> >def]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.report :as report]
   [dinsro.joins :as j]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.rates :as m.rates]
   #?(:clj [dinsro.queries.currencies :as q.currencies])
   #?(:clj [dinsro.queries.debits :as q.debits])
   #?(:clj [dinsro.queries.rates :as q.rates])
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [../actions/debits.clj]
;; [../model/debits.cljc]
;; [../queries/debits.clj]
;; [../ui/debits.cljs]

(def join-info
  (merge
   {:idents m.debits/idents}
   #?(:clj {:indexer q.debits/index-ids
            :counter q.debits/count-ids})))

(defattr admin-index
  "All debits regardless of user"
  ::admin-index :ref
  {ao/target    ::m.debits/id
   ao/pc-output [{::admin-index [:total {:results [::m.debits/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::admin-index (j/make-admin-indexer join-info env props)})})

(defattr index
  "All debits belonging to authenticated user"
  ::index :ref
  {ao/target    ::m.debits/id
   ao/pc-output [{::index [:total {:results [::m.debits/id]}]}]
   ao/pc-resolve
   (fn [env props]
     {::index (j/make-indexer join-info env props)})})

(defattr currency
  "The currency associatid with this debit"
  ::currency :ref
  {ao/target    ::m.debits/id
   ao/pc-input  #{::m.debits/account ::m.debits/id}
   ao/pc-output [{::currency [::m.currencies/id]}]
   ao/pc-resolve
   (fn [_ {debit-id ::m.debits/id}]
     (log/info :currency/starting {:debit-id debit-id})
     (let [currency-id #?(:clj (when debit-id (q.currencies/find-by-debit debit-id))
                          :cljs (do (comment debit-id) nil))
           ident       (when currency-id (m.currencies/ident currency-id))]
       (log/info :currency/id {:debit-id debit-id :ident ident})
       {::currency ident}))
   ::report/column-EQL {::currency [::m.currencies/id ::m.currencies/name]}})

(defattr current-rate ::current-rate :ref
  {ao/target    ::m.rates/id
   ao/pc-input  #{::m.debits/id}
   ao/pc-output [{::current-rate [::m.rates/id]}]
   ao/pc-resolve
   (fn [_ props]
     (let [debit-id (::m.debits/id props)
           id #?(:clj (q.rates/find-for-debit debit-id)
                 :cljs (do (comment debit-id) nil))]
       {::current-rate (when id (m.rates/ident id))}))})

(defattr current-rate-value ::current-rate-value :number
  {ao/pc-input #{::current-rate
                 ::m.debits/id}
   ao/pc-output [::current-rate-value]
   ao/pc-resolve
   (fn [_ {{::m.rates/keys [rate]} ::current-rate :as props}]
     (log/trace :current-rate-value/starting {:props props})
     {::current-rate-value rate})})

(>def ::event-value number?)
(defattr event-value ::event-value :number
  {ao/pc-input  #{::m.debits/value ::current-rate}
   ao/pc-output [::event-value]
   ao/pc-resolve
   (fn [_ {::m.debits/keys [value] ::keys [current-rate] :as props}]
     (log/trace :event-value/starting {:props props})
     (let [current-rate-value #?(:clj (some->
                                       current-rate ::m.rates/id
                                       q.rates/read-record
                                       ::m.rates/rate)
                                 :cljs (do (comment current-rate) nil))]
       {::event-value (when current-rate-value (* value (or current-rate-value 0)))}))})

(>def ::positive? boolean?)
(defattr positive? ::positive? :boolean
  {ao/pc-input #{::m.debits/id ::m.debits/value}
   ao/pc-output [::positive?]
   ao/pc-resolve (fn [_ {::m.debits/keys [value]}] {::positive? (pos? value)})})

(def attributes [current-rate-value event-value index admin-index
                 currency current-rate positive?])
