(ns dinsro.actions.rate-sources
  (:require [clojure.data.json :as json]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.rate-sources :as m.rate-sources]
            [dinsro.spec.actions.rate-sources :as s.a.rate-sources]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [dinsro.utils :as utils]
            [java-time :as t]
            [orchestra.core :refer [defn-spec]]
            [org.httpkit.client :as client]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

;; Create

(defn-spec prepare-record (s/nilable ::s.rate-sources/params)
  [params :create-rate-sources-request/params]
  (let [params {::s.rate-sources/currency {:db/id (:currency-id params)}
                ::s.rate-sources/name (some-> params :name)
                ::s.rate-sources/url (some-> params :url)}]
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
  (if-let [id (some-> (get-in request [:path-params :id]) utils/try-parse-int)]
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

(defn-spec fetch-rate ::ds/valid-double
  [item ::s.rate-sources/item]
  (let [url (::s.rate-sources/url item)
        response (client/get url)
        body (some-> @response :body (json/read-str :key-fn keyword))]
    (when-let [price (some-> body :price utils/parse-double)]
      (/ 100000000 price))))

(defn run-handler
  [request]
  (let [id (some-> (get-in request [:path-params :id]) utils/try-parse-int)]
    (if-let [item (m.rate-sources/read-record id)]
      (try
        (let [rate (fetch-rate item)]
          (http/ok {:status :ok
                    :rate-source-id id
                    :rate rate}))
        (catch NumberFormatException e
          (http/internal-server-error {:status :error :message (.getMessage e)})))
      (http/not-found {:status :not-found}))))
