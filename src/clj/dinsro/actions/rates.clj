(ns dinsro.actions.rates
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.db.core :as db]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.actions.rates :as s.a.rates]
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

(defn-spec create-handler ::s.a.rates/create-handler-response
  [request ::s.a.rates/create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.rates/create-record params)]
            (http/ok {:item (m.rates/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(defn-spec index-handler ::s.a.rates/index-handler-response
  [request ::s.a.rates/index-handler-request]
  (let [items (m.rates/index-records)
        limit 50
        items (sort (fn [a b] (.compareTo (::s.rates/date b) (::s.rates/date a))) items)
        items (take 50 items)]
    (http/ok {:model :rates
              :limit limit
              :items items})))

;; Read

(defn-spec read-handler ::s.a.rates/read-handler-response
  [request ::s.a.rates/read-handler-request]
  (or (let [params (:path-params request)]
        (let [id (:id params)]
          (let [item (m.rates/read-record id)]
            (http/ok {:item item}))))
      (http/not-found {:status :not-found})))

;; Delete

(defn-spec delete-handler ::s.a.rates/delete-handler-response
  [request ::s.a.rates/delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.rates/delete-record id)
    (http/ok {:id id})))
