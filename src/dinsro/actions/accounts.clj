(ns dinsro.actions.accounts
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.actions.accounts :as s.a.accounts]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::s.accounts/name
   :initial-value ::s.accounts/initial-value})

;; Prepare

(defn prepare-record
  [params]
  (let [currency-id (some-> params :currency-id)
        user-id (some-> params :user-id)
        initial-value (some-> params :initial-value str utils/try-parse-double)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::s.accounts/initial-value] (and initial-value (double initial-value)))
                   (assoc-in [::s.accounts/user :db/id] user-id))
        params (merge
                (when (and currency-id (not= currency-id 0))
                  {::s.accounts/currency {:db/id currency-id}})
                params)]
    (if (s/valid? ::s.accounts/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.accounts/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.accounts/create-params)
  :ret  (s/nilable ::s.accounts/params))

;; Create

(defn create-handler
  [{:keys [params]}]
  (or (when-let [params (prepare-record params)]
        (let [id (m.accounts/create-record params)]
          (http/ok {:item (m.accounts/read-record id)})))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.accounts/create-request)
  :ret ::s.a.accounts/create-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [account (m.accounts/read-record id)]
      (http/ok {:item account})
      (http/not-found {}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.accounts/read-request)
  :ret ::s.a.accounts/read-response)

;; Delete

(defn delete-handler
  [{{:keys [id]} :path-params}]
  (if-let [id (utils/try-parse-int id)]
    (do
      (m.accounts/delete-record id)
      (http/ok {:status :ok}))
    (http/bad-request {:input :invalid})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.accounts/delete-request)
  :ret ::s.a.accounts/delete-response)

;; Index

(defn index-handler
  [request]
  (if-let [user-id (:identity (:session request))]
    (let [accounts (m.accounts/index-records-by-user user-id)]
      (http/ok {:items accounts}))

    ;; FIXME: user is not authenticated. Shouldn't pass filter
    (http/bad-request {:input :invalid})))

(s/fdef index-handler
  :args (s/cat :request ::s.a.accounts/index-request)
  :ret ::s.a.accounts/index-response)

(defn index-by-category-handler
  [_]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))

(defn index-by-currency-handler
  [_]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))
