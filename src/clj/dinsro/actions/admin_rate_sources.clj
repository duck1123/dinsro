(ns dinsro.actions.admin-rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.spec.actions.admin-rate-sources :as s.a.admin-rate-sources]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

;; Prepare

(defn prepare-record
  [params]
  (let [params {::s.rate-sources/currency {:db/id (:currency-id params)}
                ::s.rate-sources/name (some-> params :name)
                ::s.rate-sources/url (some-> params :url)}]
    (if (s/valid? ::s.rate-sources/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::s.rate-sources/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.admin-rate-sources/create-params)
  :ret  (s/nilable ::s.rate-sources/params))

;; Create

(defn create-handler
  [request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.rate-sources/create-record params)]
            (http/ok {:item (m.rate-sources/read-record id)}))))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.admin-rate-sources/create-request)
  :ret ::s.a.admin-rate-sources/create-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (get-in request [:path-params :id])]
    (if-let [item (m.rate-sources/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.admin-rate-sources/read-request)
  :ret ::s.a.admin-rate-sources/read-response)

;; Delete

(defn delete-handler
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.rate-sources/delete-record id)
    (http/ok {:id id})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.admin-rate-sources/delete-request)
  :ret ::s.a.admin-rate-sources/delete-response)

;; Index

(defn index-handler
  [_request]
  (let [
        ;; TODO: parse from request
        limit 50
        items (m.rate-sources/index-records)
        response {:model :rate-sources
                  :limit limit
                  :items items}]
    (http/ok response)))

(s/fdef index-handler
  :args (s/cat :request ::s.a.admin-rate-sources/index-request)
  :ret ::s.a.admin-rate-sources/index-response)
