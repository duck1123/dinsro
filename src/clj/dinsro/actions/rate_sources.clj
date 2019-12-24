(ns dinsro.actions.rate-sources
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.rate-sources :as m.rate-sources]
            [dinsro.spec.actions.rate-sources :as s.a.rate-sources]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [java-time :as t]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

;; Create

(defn-spec prepare-record (s/nilable ::s.rate-sources/params)
  [params :create-rate-sources-request/params]
  (let [params {::s.rate-sources/currency {:db/id (:currency-id params)}
                ::s.rate-sources/name (some-> params :name)
                ::s.rate-sources/url (some-> params :url)
                ;; ::s.rate-sources/date (tick/instant (:date params))

                }]
    (if (s/valid? ::s.rate-sources/params params)
      params
      (do
        #_(timbre/warnf "not valid: %s" (expound/expound-str ::s.rate-sources/params params))
        nil))))

(defn-spec create-handler ::s.a.rate-sources/create-handler-response
  [request ::s.a.rate-sources/create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.rate-sources/create-record params)]
            (http/ok {:item (m.rate-sources/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(defn-spec index-handler ::s.a.rate-sources/index-handler-response
  [request ::s.a.rate-sources/index-handler-request]
  (let [
        ;; TODO: parse from request
        limit 50
        items (m.rate-sources/index-records)]
    (let [response {:model :rate-sources
                    :limit limit
                    :items items}]
      (http/ok response))))

;; Read

(defn-spec read-handler ::s.a.rate-sources/read-handler-response
  [request ::s.a.rate-sources/read-handler-request]
  (if-let [id (timbre/spy :info (get-in request :path-params :id))]
    (if-let [item (m.rate-sources/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

;; Delete

(defn-spec delete-handler ::s.a.rate-sources/delete-handler-response
  [request ::s.a.rate-sources/delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.rate-sources/delete-record id)
    (http/ok {:id id})))
