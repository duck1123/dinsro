(ns dinsro.actions.admin-accounts
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [dinsro.model.accounts :as m.accounts]
            [dinsro.spec.actions.admin-accounts :as s.a.admin-accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.utils :as utils]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::s.accounts/name
   :initial-value ::s.accounts/initial-value})

(defn get-as-int
  [params key]
  (try
    (some-> params key str Integer/parseInt)
    (catch NumberFormatException _ nil)))

;; Prepare

(defn prepare-record
  [params]
  (let [currency-id (get-as-int params :currency-id)
        user-id (get-as-int params :user-id)
        initial-value (some-> params :initial-value str Double/parseDouble)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::s.accounts/initial-value] initial-value)
                   (assoc-in [::s.accounts/currency :db/id] currency-id)
                   (assoc-in [::s.accounts/user :db/id] user-id))]
    (if (s/valid? ::s.accounts/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::s.accounts/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.admin-accounts/create-params)
  :ret  (s/nilable ::s.accounts/params))

;; Create

(defn create-handler
  [{:keys [params]}]
  (or (when-let [params (prepare-record params)]
        (let [id (m.accounts/create-record params)]
          (http/ok {:item (m.accounts/read-record id)})))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.admin-accounts/create-request)
  :ret ::s.a.admin-accounts/create-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [account (m.accounts/read-record {:id id})]
      (http/ok account)
      (http/not-found {}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.admin-accounts/read-request)
  :ret ::s.a.admin-accounts/read-response)

;; Delete

(defn delete-handler
  [{{:keys [id]} :path-params}]
  (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
    (do
      (m.accounts/delete-record id)
      (http/ok {:status :ok}))
    (http/bad-request {:input :invalid})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.admin-accounts/delete-request)
  :ret ::s.a.admin-accounts/delete-response)

;; Index

(defn index-handler
  [_request]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))

(s/fdef index-handler
  :args (s/cat :request ::s.a.admin-accounts/index-request)
  :ret ::s.a.admin-accounts/index-response)
