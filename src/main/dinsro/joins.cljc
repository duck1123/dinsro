(ns dinsro.joins
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [=> >def >defn]]
   [taoensso.timbre :as log]))

(>def ::join-info (s/keys))

;; A function that performs counting
(>def ::counter fn?)

;; A function that performs indexing
(>def ::indexer fn?)

;; function that converts an id into an ident
(>def ::idents fn?)

(>def ::index-result (s/keys))
(>def ::flat-index-result (s/coll-of (s/keys)))

(>defn make-indexer
  "Takes join info, a pathom resolver environment and properties and returns the results if indexing that model"
  [join-info {:keys [query-params]} _]
  [::join-info map? map? => ::index-result]
  (log/info :make-indexer/starting {:join-info join-info :query-params query-params})
  (let [indexer #?(:clj (:indexer join-info) :cljs (fn [_] []))
        counter #?(:clj (:counter join-info) :cljs (fn [_] []))
        idents  (:idents join-info)
        ids     (indexer query-params)
        total   (counter query-params)
        results (idents ids)
        indexed-result {:total total :results results}]
    (log/info :make-indexer/finished {:indexed-result indexed-result})
    indexed-result))

(>defn make-admin-indexer
  "Add admin flax then run make-indexer"
  [join-info env props]
  [::join-info map? map? => ::index-result]
  (log/info :make-flat-admin-indexer/starting {:join-info join-info :prop props})
  (let [env (assoc-in env [:query-params :actor/admin?] true)]
    (make-indexer join-info env props)))

(>defn make-flat-indexer
  [join-info {:keys [query-params]} _]
  [::join-info map? map? => ::flat-index-result]
  (log/info :make-indexer/starting {:join-info join-info :query-params query-params})
  (let [indexer #?(:clj (:indexer join-info) :cljs (fn [_] []))
        idents  (:idents join-info)
        ids     (indexer query-params)
        results (idents ids)]
    (log/info :make-flat-indexer/finished {:results results})
    results))

(>defn make-flat-admin-indexer
  "Add admin flax then run make-indexer"
  [join-info env props]
  [::join-info map? map? => ::flat-index-result]
  (log/info :make-flat-admin-indexer/starting {:join-info join-info :prop props})
  (let [env (assoc-in env [:query-params :actor/admin?] true)]
    (make-flat-indexer join-info env props)))
