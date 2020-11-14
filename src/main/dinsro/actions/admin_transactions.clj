(ns dinsro.actions.admin-transactions
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs.actions.admin-transactions :as s.a.admin-transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

;; Prepare

(defn prepare-record
  [params]
  (let [account-id (utils/get-as-int params :account-id)
        params {::m.transactions/value (some-> params :value str Double/parseDouble)
                ::m.transactions/account {:db/id account-id}
                ::m.transactions/date (some-> params :date str tick/instant)}]
    (if (s/valid? ::m.transactions/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.transactions/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.admin-transactions/create-params)
  :ret  (s/nilable ::m.transactions/params))

;; Create

(defn create-handler
  [request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (q.transactions/create-record params)]
            (http/ok {:item (q.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.admin-transactions/create-request)
  :ret ::s.a.admin-transactions/create-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [item (q.transactions/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.admin-transactions/read-request)
  :ret ::s.a.admin-transactions/read-response)

;; Delete

(defn delete-handler
  [request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (do
      (q.transactions/delete-record id)
      (http/ok {:id id}))
    (http/bad-request
     {:status :bad-request
      :message (tr [:missing-id "Id parameter was not supplied"])})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.admin-transactions/delete-request)
  :ret ::s.a.admin-transactions/delete-response)

;; Index

(defn index-handler
  [_]
  (let [items (q.transactions/index-records)]
    (http/ok {:model :transactions :items items})))

(s/fdef index-handler
  :args (s/cat :request ::s.a.admin-transactions/index-request)
  :ret ::s.a.admin-transactions/index-response)
