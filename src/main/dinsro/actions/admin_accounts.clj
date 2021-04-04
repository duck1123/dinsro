(ns dinsro.actions.admin-accounts
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
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

(>defn prepare-record
  [params]
  [::s.a.admin-accounts/create-params => (? ::m.accounts/params)]
  (let [currency-id   (some-> params :currency-id utils/try-parse-int)
        user-id       (some-> params :user-id utils/try-parse-int)
        initial-value (some-> params :initial-value utils/try-parse-double)
        params        (-> params
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

(>defn create-handler
  [{:keys [params]}]
  [::s.a.admin-accounts/create-request => ::s.a.admin-accounts/create-response]
  (or (when-let [params (prepare-record params)]
        (let [id (q.accounts/create-record params)]
          (http/ok {:item (q.accounts/read-record id)})))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.admin-accounts/read-request => ::s.a.admin-accounts/read-response]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [account (q.accounts/read-record {:id id})]
      (http/ok account)
      (http/not-found {}))
    (http/bad-request {:status :bad-request})))

(>defn delete-handler
  [{{:keys [id]} :path-params}]
  [::s.a.admin-accounts/delete-request => ::s.a.admin-accounts/delete-response]
  (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
    (do
      (q.accounts/delete-record id)
      (http/ok {:status :ok}))
    (http/bad-request {:input :invalid})))

(>defn index-handler
  [_request]
  [::s.a.admin-accounts/index-request => ::s.a.admin-accounts/index-response]
  (let [accounts (q.accounts/index-records)]
    (http/ok {:items accounts})))
