(ns dinsro.actions.rates
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [java-time :as t]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

;; Create

(def param-rename-map
  {:rate       ::s.rates/rate
   ;; :currency-id ::s.rates/currency-id
   :date      ::s.rates/date})

(s/def ::currency-id ::ds/id)
(s/def :create-rates-request-valid/params (s/keys :req-un [::s.rates/rate ::currency-id]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-rates-request-valid/params]))

(s/def :create-rates-request/params (s/keys :opt-un [::s.rates/rate ::currency-id]))
(s/def ::create-handler-request (s/keys :req-un [:create-rates-request/params]))

(comment
  (gen/generate (s/gen ::create-handler-request-valid))
  (gen/generate (s/gen ::create-handler-request))
  )

(s/def :create-rates-response-valid/body (s/keys :req-un [::s.rates/item]))
(s/def :create-rates-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-rates-response-valid/body
                                                        :create-rates-response-valid/status]))

(s/def :create-rates-response-invalid-body/status #{:invalid})
(s/def :create-rates-response-invalid/body (s/keys :req-un [:create-rates-response-invalid-body/status]))
(s/def :create-rates-response-invalid/status #{status/bad-request})
(s/def ::create-handler-response-invalid (s/keys :req-un [:create-rates-response-invalid/body
                                                          :create-rates-response-invalid/status]))

(s/def ::create-handler-response (s/or :invalid ::create-handler-response-invalid
                                       :valid   ::create-handler-response-valid))

(comment
  (gen/generate (s/gen ::create-handler-response-valid))
  (gen/generate (s/gen ::create-handler-response-invalid))
  (gen/generate (s/gen ::create-handler-response))
  )

(defn-spec prepare-record (s/nilable ::s.rates/params)
  [params :create-rates-request/params]
  (when-let [rate (:rate params)]
    (let [currency-id (:currency-id params)
          rate (double rate)
          date (t/java-date (:date params))
          params (-> params
                     (set/rename-keys param-rename-map)
                     (select-keys (vals param-rename-map))
                     (assoc ::s.rates/currency {:db/id currency-id})
                     (assoc ::s.rates/rate rate)
                     (assoc ::s.rates/date date))]
      (if (s/valid? ::s.rates/params params)
        params
        (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.rates/params params))
            nil)))))

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.rates/create-record params)]
            (http/ok {:item (m.rates/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(s/def :index-rates-response/items (s/coll-of ::s.rates/item))
(s/def :index-rates-response/body (s/keys :req-un [:index-rates-response/items]))
(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys :req-un [:index-rates-response/body]))

(comment
  (gen/generate (s/gen :index-rates-response/items))
  (gen/generate (s/gen :index-rates-response/body))
  (gen/generate (s/gen ::index-handler-response))
  )

(defn-spec index-handler ::index-handler-response
  [request ::index-handler-request]
  (let [items (m.rates/index-records)]
    (http/ok {:model :rates :items items})))

;; Read

(s/def :read-rates-request/path-params (s/keys :req-un []))
(s/def ::read-handler-request (s/keys :req-un [:read-rates-request/path-params]))

(comment
  (gen/generate (s/gen :read-rates-request/path-params))
  (gen/generate (s/gen ::read-handler-request))
  )

(s/def :read-rates-response/body (s/keys :req-un [::s.rates/item]))
(s/def :read-rates-response-not-found-body/status #{:not-found})
(s/def :read-rates-response-not-found/body (s/keys :req-un [:read-rates-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-rates-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-rates-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

(comment
  (gen/generate (s/gen :read-rates-response/body))
  (gen/generate (s/gen :read-rates-response/status))
  (gen/generate (s/gen :read-rates-response-not-found/body))
  (gen/generate (s/gen ::read-handler-response-valid))
  (gen/generate (s/gen ::read-handler-response-not-found))
  (gen/generate (s/gen ::read-handler-response))
  )

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (or (let [params (:path-params request)]
        (let [id (:id params)]
          (let [item (m.rates/read-record id)]
            (http/ok {:item item}))))
      (http/not-found {:status :not-found})))

;; Delete

(s/def :delete-rates-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-rates-request/path-params (s/keys :req-un [:delete-rates-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-rates-request/path-params]))

(s/def ::delete-handler-response (s/keys))

(defn-spec delete-handler ::delete-handler-response
  [request ::delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.rates/delete-record id)
    (http/ok {:id id})))

(comment
  (gen/generate (s/gen ::delete-handler-request))
  (gen/generate (s/gen ::delete-handler-response))
  )
