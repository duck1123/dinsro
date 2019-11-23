(ns dinsro.actions.currencies
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-handler/params (s/keys :opt-un [::s.currencies/name]))
(s/def :create-handler-valid/params (s/keys :req-un [::s.currencies/name]))
(s/def :create-handler-valid/request (s/keys :req-un [:create-handler-valid/params]))

(s/def :create-currency-response/item ::s.currencies/item)
(comment
  (gen/generate (s/gen :create-currency-response/item))
  )

(s/def :create-currency-response/items (s/coll-of ::s.currencies/item))
(comment
  (gen/generate (s/gen :create-currency-response/items))
  )

(s/def :create-currency-response/body (s/keys :req-un [:create-currency-response/item]))
(comment
  (gen/generate (s/gen :create-currency-response/body))
  )


(s/def :create-currency-response/status keyword?)
(comment
  (gen/generate (s/gen :create-currency-response/status))
  )


(s/def :create-currency-response-invalid/body (s/keys :req-un [:create-currency-response/status]))
(comment
  (gen/generate (s/gen :create-currency-response-invalid/body))
  )

(s/def ::create-handler-request-valid (s/keys :req-un [:create-handler-valid/params]))
(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(comment
  (gen/generate (s/gen ::create-handler-request))
  )

(s/def ::create-handler-response-invalid (s/keys :req-un [:create-currency-response-invalid/body]))
(comment
  (gen/generate (s/gen ::create-handler-response-invalid))
  )

(s/def ::create-handler-response-valid (s/keys :req-un [:create-currency-response/body]))
(comment
  (gen/generate (s/gen ::create-handler-response-valid))
  )

(s/def ::create-handler-response (s/or ::create-handler-response-valid ::create-handler-response-invalid))
(comment
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
  (let [items (m.currencies/index)]
    (http/ok {:items items})))

(comment
  (clojure.spec.gen.alpha/generate (s/gen ::m.currencies/params))

  (clojure.spec.gen.alpha/generate (s/gen ::create-handler-request))
  (prepare-record (:params (clojure.spec.gen.alpha/generate (s/gen ::create-handler-request))))
  )
