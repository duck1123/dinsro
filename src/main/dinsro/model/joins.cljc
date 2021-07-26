(ns dinsro.model.joins
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   #?(:clj [dinsro.queries.categories :as q.categories])
   #?(:clj [dinsro.queries.transactions :as q.transactions])
   [dinsro.specs]
   [taoensso.timbre :as log]))

(>defn find-by-currency
  [currency-id]
  [::m.currencies/id => (s/coll-of ::m.accounts/id)]
  (q.accounts/find-by-currency currency-id))

(defattr account-transactions ::m.accounts/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.accounts/id}
   ao/pc-output   [{::m.accounts/transactions [::m.transactions/id]}]
   ao/pc-resolve
   (fn [_env {::m.accounts/keys [id]}]
     #?(:clj  (if id
                (let [ids (q.transactions/find-by-account id)]
                  {::m.accounts/transactions (map (fn [id] {::m.transactions/id id}) ids)})
                {::m.accounts/transactions []})
        :cljs (comment id)))})

(defattr category-transactions ::m.categories/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.categories/id}
   ao/pc-output   [{::m.categories/transactions [::m.transactions/id]}]
   ao/pc-resolve
   (fn [_env {::m.categories/keys [id]}]
     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-category id)]
                  {::m.categories/transactions (map (fn [id] {::m.categories/id id}) transaction-ids)})
          :cljs {::m.categories/transactions []})
       {::m.categories/transactions []}))})

(defattr currency-accounts ::m.currencies/accounts :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/accounts [::m.accounts/id]}]
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     #?(:clj  (if id
                (let [account-ids (q.accounts/find-by-currency id)]
                  {::m.currencies/accounts (map (fn [id] {::m.accounts/id id}) account-ids)})
                {::m.currencies/accounts []})
        :cljs (comment id)))})

(defattr currency-transactions ::m.currencies/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.currencies/id}
   ao/pc-output   [{::m.currencies/transactions [::m.transactions/id]}]
   ao/pc-resolve
   (fn [_env {::m.currencies/keys [id]}]
     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-currency id)]
                  {::m.currencies/transactions (map (fn [id] {::m.transactions/id id}) transaction-ids)})
          :cljs {::m.currencies/transactions []})
       {::m.currencies/transactions []}))})

(defattr user-accounts ::m.users/accounts :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/accounts [::m.accounts/id]}]
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     #?(:clj  (if id
                (let [account-ids (q.accounts/find-by-user id)]
                  {::m.users/accounts (map (fn [id] {::m.accounts/id id}) account-ids)})
                {::m.users/accounts []})
        :cljs (comment id)))})

(defattr user-categories ::m.users/categories :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/categories [::m.categories/id]}]
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]
     #?(:clj  (if id
                (let [category-ids (q.categories/find-by-user id)]
                  {::m.users/categories (map (fn [id] {::m.categories/id id}) category-ids)})
                {::m.users/categories []})
        :cljs (comment id)))})

(defattr user-transactions ::m.users/transactions :ref
  {ao/cardinality :many
   ao/pc-input    #{::m.users/id}
   ao/pc-output   [{::m.users/transactions [::m.transactions/id]}]
   ao/pc-resolve
   (fn [_env {::m.users/keys [id]}]

     (if id
       #?(:clj  (let [transaction-ids (q.transactions/find-by-user id)]
                  {::m.users/transactions (map (fn [id] {::m.transactions/id id}) transaction-ids)})
          :cljs {::m.users/transactions []})
       {::m.users/transactions []}))})

(def attributes
  [account-transactions
   category-transactions
   currency-accounts
   currency-transactions
   user-accounts
   user-categories
   user-transactions])
