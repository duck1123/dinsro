(ns dinsro.actions.currencies
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(s/def :create-currency-request/params (s/keys :opt-un [::s.currencies/name]))
(s/def :create-currency-request-valid/params (s/keys :req-un [::s.currencies/name]))
(s/def :create-currency-request-valid/request (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))

(comment
  (gen/generate (s/gen ::create-handler-request-valid))
  (gen/generate (s/gen ::create-handler-request))

  )

(s/def :create-currency-response-invalid-body/status #{:invalid})
(s/def :create-currency-response-invalid/status #{status/bad-request})
(s/def :create-currency-response-invalid/body (s/keys :req-un [:create-currency-response-invalid-body/status]))
(s/def ::create-handler-response-invalid (s/keys :req-un [:create-currency-response-invalid/body
                                                          :create-currency-response-invalid/status]))

(s/def :create-currency-response/item ::s.currencies/item)
(s/def :create-currency-response/body (s/keys :req-un [:create-currency-response/item]))
(s/def ::create-handler-response-valid (s/keys :req-un [:create-currency-response/body]))

(s/def ::create-handler-response (s/or :valid   ::create-handler-response-valid
                                       :invalid ::create-handler-response-invalid))

(comment
  (gen/generate (s/gen :create-currency-response/item))
  (gen/generate (s/gen :create-currency-response/items))
  (gen/generate (s/gen :create-currency-response/body))
  (gen/generate (s/gen :create-currency-response-invalid/status))
  (gen/generate (s/gen :create-currency-response-invalid/body))
  (gen/generate (s/gen ::create-handler-response-invalid))
  (gen/generate (s/gen ::create-handler-response-valid))
  (gen/generate (s/gen ::create-handler-response))
  )

(def param-rename-map
  {:name ::s.currencies/name})

(defn-spec prepare-record (s/nilable ::s.currencies/params)
  [params :create-handler/params]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (when (s/valid? ::s.currencies/params params)
      params)))

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (or (let [{:keys [params]} request]
        (when-let [params (prepare-record params)]
          (let [id (m.currencies/create-record params)]
            (http/ok {:item (m.currencies/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Delete

(s/def ::delete-handler-response (s/keys))
(s/def ::delete-handler-request (s/keys))

(defn-spec delete-handler ::delete-handler-response
  [request ::delete-handler-request]
  (let [{{:keys [id]} :path-params} request]
    (or (try
          (let [id (Integer/parseInt id)]
            (m.currencies/delete-record id)
            (http/ok {:id id}))
          (catch NumberFormatException e nil))
        (http/bad-request {:status :invalid}))))

;; Index

(defn index-handler
  [request]
  (let [items (m.currencies/index-records)]
    (http/ok {:items items})))

(comment
  (clojure.spec.gen.alpha/generate (s/gen ::m.currencies/params))

  (clojure.spec.gen.alpha/generate (s/gen ::create-handler-request))
  (prepare-record (:params (clojure.spec.gen.alpha/generate (s/gen ::create-handler-request))))
  )

;; Read

(s/def :read-currency-request-path-params/id
  (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :read-currency-request/path-params (s/keys :req-un [:read-currency-request-path-params/id]))
(s/def ::read-handler-request (s/keys :req-un [:read-currency-request/path-params]))

(s/def :read-currency-response-body/item ::s.currencies/item)
(s/def :read-currency-response-success/body
  (s/keys :req-un [:read-currency-response-body/item]))
(s/def ::read-handler-response-success
  (s/keys :req-un [:read-currency-response-success/body]))

(s/def :read-currency-response-not-found-body/status keyword?)
(s/def :read-currency-response-not-found/body
  (s/keys :req-un [:read-currency-response-not-found-body/status]))
(s/def ::read-handler-response-not-found
  (s/keys :req-un [:read-currency-response-not-found/body]))

(s/def ::read-handler-response
  (s/or :success   ::read-handler-response-success
        :not-found ::read-handler-response-not-found))

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (let [id (some-> request :path-params :id Integer/parseInt)]
    (if-let [item (m.currencies/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))))

(comment

  (Integer/parseInt (str 1))

  (gen/generate (s/gen ::read-handler-request))
  (gen/generate (s/gen ::read-handler-response-success))
  (gen/generate (s/gen ::read-handler-response-not-found))
  (gen/generate (s/gen ::read-handler-response))

  (read-handler {:path-params {:id "45"}})
  )
