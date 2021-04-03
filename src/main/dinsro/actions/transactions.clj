(ns dinsro.actions.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [expound.alpha :as expound]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.specs.actions.transactions :as s.a.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

;; Create

(>defn prepare-record
  [params]
  [::s.a.transactions/create-params => (? ::m.transactions/params)]
  (let [account-id (utils/get-as-int params :account-id)
        params     {::m.transactions/value       (some-> params :value str Double/parseDouble)
                    ::m.transactions/account     {::m.accounts/id account-id}
                    ::m.transactions/description (some-> params :description)
                    ::m.transactions/date        (some-> params :date str tick/instant)}]
    (if (s/valid? ::m.transactions/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::m.transactions/params params)))
        nil))))

(>defn create!
  [params]
  [::s.a.transactions/create-params => (? ::m.transactions/item)]
  (some-> params q.transactions/create-record q.transactions/read-record))

(>defn create-handler
  [{params :params}]
  [::s.a.transactions/create-request => ::s.a.transactions/create-response]
  (or (when-let [item (some-> params prepare-record create!)]
        (http/ok {:item item}))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.transactions/read-request => ::s.a.transactions/read-response]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [item (q.transactions/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(>defn delete-handler
  [request]
  [::s.a.transactions/delete-request => ::s.a.transactions/delete-response]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (do
      (q.transactions/delete-record id)
      (http/ok {:id id}))
    (http/bad-request
     {:status  :bad-request
      :message (tr [:missing-id "Id parameter was not supplied"])})))

(>defn index-handler
  [_]
  [::s.a.transactions/index-request => ::s.a.transactions/index-response]
  (let [items (q.transactions/index-records)]
    (http/ok {:model :transactions :items items})))
