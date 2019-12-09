(ns dinsro.actions.transactions
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [java-time :as t]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

;; Create

(def param-rename-map
  {:value       ::s.transactions/value
   ;; :currency-id ::s.transactions/currency-id
   :date      ::s.transactions/date})

(s/def ::currency-id ::ds/id)
(s/def :create-transactions-request-valid/params
  (s/keys :req-un [::s.transactions/value ::currency-id]))
(s/def ::create-handler-request-valid (s/keys :req-un [:create-transactions-request-valid/params]))

(s/def :create-transactions-request/params (s/keys :opt-un [::s.transactions/transaction ::currency-id]))
(s/def ::create-handler-request (s/keys :req-un [:create-transactions-request/params]))

(comment
  (gen/generate (s/gen ::create-handler-request-valid))
  (gen/generate (s/gen ::create-handler-request))
  )

(s/def :create-transactions-response-valid/body (s/keys :req-un [::s.transactions/item]))
(s/def :create-transactions-response-valid/status #{status/ok})
(s/def ::create-handler-response-valid (s/keys :req-un [:create-transactions-response-valid/body
                                                        :create-transactions-response-valid/status]))

(s/def :create-transactions-response-invalid-body/status #{:invalid})
(s/def :create-transactions-response-invalid/body (s/keys :req-un [:create-transactions-response-invalid-body/status]))
(s/def :create-transactions-response-invalid/status #{status/bad-request})
(s/def ::create-handler-response-invalid (s/keys :req-un [:create-transactions-response-invalid/body
                                                          :create-transactions-response-invalid/status]))

(s/def ::create-handler-response (s/or :invalid ::create-handler-response-invalid
                                       :valid   ::create-handler-response-valid))

(comment
  (gen/generate (s/gen ::create-handler-response-valid))
  (gen/generate (s/gen ::create-handler-response-invalid))
  (gen/generate (s/gen ::create-handler-response))
  )

(defn-spec prepare-record (s/nilable ::s.transactions/params)
  [params :create-transactions-request/params]
  (when-let [transaction (:transaction params)]
    (let [currency-id (:currency-id params)
          transaction (double transaction)
          date (t/java-date (:date params))
          params (-> params
                     (set/rename-keys param-rename-map)
                     (select-keys (vals param-rename-map))
                     (assoc ::s.transactions/currency {:db/id currency-id})
                     (assoc ::s.transactions/transaction transaction)
                     (assoc ::s.transactions/date date))]
      (if (s/valid? ::s.transactions/params params)
        params
        (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.transactions/params params))
            nil)))))

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.transactions/create-record params)]
            (http/ok {:item (m.transactions/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(s/def :index-transactions-response/items (s/coll-of ::s.transactions/item))
(s/def :index-transactions-response/body (s/keys :req-un [:index-transactions-response/items]))
(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys :req-un [:index-transactions-response/body]))

(comment
  (gen/generate (s/gen :index-transactions-response/items))
  (gen/generate (s/gen :index-transactions-response/body))
  (gen/generate (s/gen ::index-handler-response))
  )

(defn-spec index-handler ::index-handler-response
  [request ::index-handler-request]
  (let [items (m.transactions/index-records)]
    (http/ok {:model :transactions :items items})))

;; Read

(s/def :read-transactions-request/path-params (s/keys :req-un []))
(s/def ::read-handler-request (s/keys :req-un [:read-transactions-request/path-params]))

(comment
  (gen/generate (s/gen :read-transactions-request/path-params))
  (gen/generate (s/gen ::read-handler-request))
  )

(s/def :read-transactions-response/body (s/keys :req-un [::s.transactions/item]))
(s/def :read-transactions-response-not-found-body/status #{:not-found})
(s/def :read-transactions-response-not-found/body (s/keys :req-un [:read-transactions-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-transactions-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-transactions-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

(comment
  (gen/generate (s/gen :read-transactions-response/body))
  (gen/generate (s/gen :read-transactions-response/status))
  (gen/generate (s/gen :read-transactions-response-not-found/body))
  (gen/generate (s/gen ::read-handler-response-valid))
  (gen/generate (s/gen ::read-handler-response-not-found))
  (gen/generate (s/gen ::read-handler-response))
  )

(defn-spec read-handler ::read-handler-response
  [request ::read-handler-request]
  (or (let [params (:path-params request)]
        (let [id (:id params)]
          (let [item (m.transactions/read-record id)]
            (http/ok {:item item}))))
      (http/not-found {:status :not-found})))

;; Delete

(s/def :delete-transactions-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-transactions-request/path-params (s/keys :req-un [:delete-transactions-request-params/id]))
(s/def ::delete-handler-request (s/keys :req-un [:delete-transactions-request/path-params]))

(s/def ::delete-handler-response (s/keys))

(defn-spec delete-handler ::delete-handler-response
  [request ::delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.transactions/delete-record id)
    (http/ok {:id id})))

(comment
  (gen/generate (s/gen ::delete-handler-request))
  (gen/generate (s/gen ::delete-handler-response))
  )
