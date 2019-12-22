(ns dinsro.actions.transactions
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.spec.actions.transactions :as s.a.transactions]
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

(defn-spec prepare-record (s/nilable :create-transactions-request/params)
  [params :create-transactions-request/params]
  (let [currency-id (some-> params :currency-id int)
        account-id (some-> params :account-id int)
        value (some-> params :value double)
        date (some-> params :date t/java-date)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc ::s.transactions/currency {:db/id currency-id})
                   (assoc ::s.transactions/value value)
                   (assoc ::s.transactions/account {:db/id account-id})
                   (assoc ::s.transactions/date date))]
    (if (s/valid? ::s.transactions/params (timbre/spy :info params))
      params
      (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.transactions/params params))
          nil))))

(defn-spec create-handler ::s.a.transactions/create-handler-response
  [request ::s.a.transactions/create-handler-request]
  (or (let [{params :params} (timbre/spy :info request)]
        (when-let [params (prepare-record params)]
          (when-let [id (m.transactions/create-record params)]
            (http/ok {:item (m.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

(comment
  (gen/generate (s/gen ::s.a.transactions/create-handler-response))
  )


;; Index

(defn-spec index-handler ::s.a.transactions/index-handler-response
  [request ::s.a.transactions/index-handler-request]
  (let [items (m.transactions/index-records)]
    (http/ok {:model :transactions :items items})))

;; Read

(defn-spec read-handler ::s.a.transactions/read-handler-response
  [request ::s.a.transactions/read-handler-request]
  (or (let [params (:path-params request)]
        (let [id (:id params)]
          (let [item (m.transactions/read-record id)]
            (http/ok {:item item}))))
      (http/not-found {:status :not-found})))

;; Delete

(defn-spec delete-handler ::s.a.transactions/delete-handler-response
  [request ::s.a.transactions/delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.transactions/delete-record id)
    (http/ok {:id id})))
