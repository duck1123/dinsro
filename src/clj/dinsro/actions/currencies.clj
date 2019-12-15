(ns dinsro.actions.currencies
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.spec.actions.currencies :as s.a.currencies]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [expound.alpha :as expound]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name ::s.currencies/name})

(defn-spec prepare-record (s/nilable ::s.currencies/params)
  [params :create-currency-request/params]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (if (s/valid? ::s.currencies/params params)
      params
      (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.currencies/params params))
          nil))))

(defn-spec create-handler ::s.a.currencies/create-handler-response
  [request ::s.a.currencies/create-handler-request]
  (or (let [{:keys [params]} request]
        (when-let [params (prepare-record params)]
          (let [id (m.currencies/create-record params)]
            (http/ok {:item (m.currencies/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Delete

(defn-spec delete-handler ::s.a.currencies/delete-handler-response
  [request ::s.a.currencies/delete-handler-request]
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

;; Read

(defn-spec read-handler ::s.a.currencies/read-handler-response
  [request ::s.a.currencies/read-handler-request]
  (let [id (some-> request :path-params :id Integer/parseInt)]
    (if-let [item (m.currencies/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))))
