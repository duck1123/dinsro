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
            [dinsro.translations :refer [tr]]
            [dinsro.utils :as utils]
            [java-time :as t]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]
            [time-specs.core :as ts]))

;; Create

(def param-rename-map
  {:value       ::s.transactions/value
   ;; :currency-id ::s.transactions/currency-id
   :date        ::s.transactions/date})

(defn-spec prepare-record (s/nilable s.a.transactions/create-params-valid)
  [params s.a.transactions/create-request-params]
  (let [currency-id (utils/get-as-int (timbre/spy :info params) :currency-id)
        account-id (utils/get-as-int params :account-id)
        params {::s.transactions/currency {:db/id currency-id}
                ::s.transactions/value (some-> params :value double)
                ::s.transactions/account {:db/id account-id}
                ::s.transactions/date (some-> params :date tick/instant)}]
    (if (s/valid? ::s.transactions/params (timbre/spy :info params))
      params
      (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.transactions/params params))
          nil))))

(comment
  (ds/gen-key s.a.transactions/create-params)
  (ds/gen-key s.a.transactions/create-params-valid)
  )


(defn-spec create-handler s.a.transactions/create-response
  [request ::s.a.transactions/create-handler-request]
  (or (let [{params :params} (timbre/spy :info request)]
        (when-let [params (prepare-record params)]
          (when-let [id (m.transactions/create-record params)]
            (http/ok {:item (m.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

(comment
  (ds/gen-key ::s.a.transactions/create-handler-response)
  )


;; Index

(defn-spec index-handler ::s.a.transactions/index-handler-response
  [request ::s.a.transactions/index-handler-request]
  (let [items (m.transactions/index-records)]
    (http/ok {:model :transactions :items items})))

;; Read

(defn-spec read-handler ::s.a.transactions/read-handler-response
  [request ::s.a.transactions/read-handler-request]
  (if-let [id (some-> request :path-params :id utils/try-parse)]
    (if-let [item (m.transactions/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

;; Delete

(defn-spec delete-handler ::s.a.transactions/delete-handler-response
  [request ::s.a.transactions/delete-handler-request]
  (if-let [id (some-> request :path-params :id utils/try-parse)]
    (do
      (m.transactions/delete-record id)
      (http/ok {:id id}))
    (http/bad-request
     {:status :bad-request
      :message (tr [:missing-id "Id parameter was not supplied"])})))
