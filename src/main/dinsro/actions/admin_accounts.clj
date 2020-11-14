(ns dinsro.actions.admin-accounts
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs.actions.admin-accounts :as s.a.admin-accounts]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::m.accounts/name
   :initial-value ::m.accounts/initial-value})

;; Prepare

(defn prepare-record
  [params]
  (let [currency-id (some-> params :currency-id utils/try-parse-int)
        user-id (some-> params :user-id utils/try-parse-int)
        initial-value (some-> params :initial-value utils/try-parse-double)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::m.accounts/initial-value] initial-value)
                   (assoc-in [::m.accounts/currency :db/id] currency-id)
                   (assoc-in [::m.accounts/user :db/id] user-id))]
    (if (s/valid? ::m.accounts/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.accounts/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.admin-accounts/create-params)
  :ret  (s/nilable ::m.accounts/params))

;; Create

(defn create-handler
  [{:keys [params]}]
  (or (when-let [params (prepare-record params)]
        (let [id (q.accounts/create-record params)]
          (http/ok {:item (q.accounts/read-record id)})))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.admin-accounts/create-request)
  :ret ::s.a.admin-accounts/create-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [account (q.accounts/read-record {:id id})]
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
      (q.accounts/delete-record id)
      (http/ok {:status :ok}))
    (http/bad-request {:input :invalid})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.admin-accounts/delete-request)
  :ret ::s.a.admin-accounts/delete-response)

;; Index

(defn index-handler
  [_request]
  (let [accounts (q.accounts/index-records)]
    (http/ok {:items accounts})))

(s/fdef index-handler
  :args (s/cat :request ::s.a.admin-accounts/index-request)
  :ret ::s.a.admin-accounts/index-response)
