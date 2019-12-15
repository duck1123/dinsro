(ns dinsro.actions.transactions
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [java-time :as t]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

;; Create

(def param-rename-map
  {:value       ::s.transactions/value
   ;; :currency-id ::s.transactions/currency-id
   :date      ::s.transactions/date})

(defn-spec prepare-record (s/nilable ::s.transactions/params)
  [params :create-transactions-request/params]
  (when-let [transaction (:transaction params)]
    (let [currency-id (:currency-id params)
          transaction (double transaction)
          date (t/java-date (:date params))
          params (-> params
                     (set/rename-keys param-rename-map)
                     (select-keys (vals param-rename-map))
                     (assoc ::s.transactions/currency {:db/id currency-id})
                     (assoc ::s.transactions/transaction transaction)
                     (assoc ::s.transactions/date date))]
      (if (s/valid? ::s.transactions/params params)
        params
        (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.transactions/params params))
            nil)))))

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.transactions/create-record params)]
            (http/ok {:item (m.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(defn-spec index-handler ::index-handler-response
  [request ::index-handler-request]
  (let [items (m.transactions/index-records)]
    (http/ok {:model :transactions :items items})))

;; Read

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (or (let [params (:path-params request)]
        (let [id (:id params)]
          (let [item (m.transactions/read-record id)]
            (http/ok {:item item}))))
      (http/not-found {:status :not-found})))

;; Delete

(defn-spec delete-handler ::delete-handler-response
  [request ::delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.transactions/delete-record id)
    (http/ok {:id id})))
