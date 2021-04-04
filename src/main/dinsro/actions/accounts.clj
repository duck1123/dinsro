(ns dinsro.actions.accounts
  "Actions for accounts"
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [expound.alpha :as expound]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.specs.actions.accounts :as s.a.accounts]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::m.accounts/name
   :initial-value ::m.accounts/initial-value})

(>defn prepare-record
  [params]
  [::s.a.accounts/create-params => (? ::m.accounts/params)]
  (let [currency-id   (some-> params :currency-id)
        user-id       (some-> params :user-id)
        initial-value (some-> params :initial-value str utils/try-parse-double)
        params        (-> params
                          (set/rename-keys param-rename-map)
                          (select-keys (vals param-rename-map))
                          (assoc-in [::m.accounts/initial-value]
                                    (and initial-value (double initial-value)))
                          (assoc-in [::m.accounts/user :db/id] user-id))
        params        (merge
                       (when (and currency-id (not= currency-id 0))
                         {::m.accounts/currency {:db/id currency-id}})
                       params)]
    (if (s/valid? ::m.accounts/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::m.accounts/params params)))
        nil))))

(>defn create!
  [params]
  [::s.a.accounts/create-params => (? ::m.accounts/item)]
  (some-> params q.accounts/create-record q.accounts/read-record))

(>defn create-handler
  [{:keys [params]}]
  [::s.a.accounts/create-request => ::s.a.accounts/create-response]
  (or (when-let [item (some-> params prepare-record create!)]
        (http/ok {:item item}))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.accounts/read-request => ::s.a.accounts/read-response]
  (if-let [id (some-> request :path-params :id utils/try-parse-int)]
    (if-let [account (q.accounts/read-record id)]
      (http/ok {:item account})
      (http/not-found {}))
    (http/bad-request {:status :bad-request})))

(>defn delete-handler
  [{{:keys [id]} :path-params}]
  [::s.a.accounts/delete-request => ::s.a.accounts/delete-response]
  (if-let [id (utils/try-parse-int id)]
    (do
      (q.accounts/delete-record id)
      (http/ok {:status :ok}))
    (http/bad-request {:input :invalid})))

(>defn index-handler
  [request]
  [::s.a.accounts/index-request => ::s.a.accounts/index-response]
  (if-let [user-id (:identity (:session request))]
    (let [accounts (q.accounts/index-records-by-user user-id)]
      (http/ok {:items accounts}))

    ;; FIXME: user is not authenticated. Shouldn't pass filter
    (http/bad-request {:input :invalid})))

(defn index-by-category-handler
  [_]
  (let [accounts (q.accounts/index-records)]
    (http/ok {:items accounts})))

(defn index-by-currency-handler
  [_]
  (let [accounts (q.accounts/index-records)]
    (http/ok {:items accounts})))
