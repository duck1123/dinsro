(ns dinsro.actions.transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.translations :refer [tr]]
            [dinsro.utils :as utils]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

;; Create

(defn-spec prepare-record (s/nilable s.transactions/params)
  [params s.a.transactions/create-params]
  (let [currency-id (utils/get-as-int params :currency-id)
        account-id (utils/get-as-int params :account-id)
        params {::s.transactions/currency {:db/id currency-id}
                ::s.transactions/value (some-> params :value str Double/parseDouble)
                ::s.transactions/account {:db/id account-id}
                ::s.transactions/date (some-> params :date str tick/instant)}]
    (if (s/valid? ::s.transactions/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.transactions/params params)))
        nil))))

(defn-spec create-handler s.a.transactions/create-response
  [request ::s.a.transactions/create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.transactions/create-record params)]
            (http/ok {:item (m.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(defn-spec index-handler ::s.a.transactions/index-handler-response
  [request ::s.a.transactions/index-handler-request]
  (let [items (m.transactions/index-records)]
    (http/ok {:model :transactions :items items})))

;; Read

(defn-spec read-handler ::s.a.transactions/read-handler-response
  [request ::s.a.transactions/read-handler-request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [item (m.transactions/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

;; Delete

(defn-spec delete-handler ::s.a.transactions/delete-handler-response
  [request ::s.a.transactions/delete-handler-request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (do
      (m.transactions/delete-record id)
      (http/ok {:id id}))
    (http/bad-request
     {:status :bad-request
      :message (tr [:missing-id "Id parameter was not supplied"])})))
