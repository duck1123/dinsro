(ns dinsro.actions.transactions
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
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

(defn prepare-record
  [params]
  (let [account-id (utils/get-as-int params :account-id)
        params {::s.transactions/value (some-> params :value str Double/parseDouble)
                ::s.transactions/account {:db/id account-id}
                ::s.transactions/description (some-> params :description)
                ::s.transactions/date (some-> params :date str tick/instant)}]
    (if (s/valid? ::s.transactions/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.transactions/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.transactions/create-params)
  :ret  (s/nilable ::s.transactions/params))

(defn create-handler
  [request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.transactions/create-record params)]
            (http/ok {:item (m.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.transactions/create-handler-request)
  :ret ::s.a.transactions/create-response)

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
