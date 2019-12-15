(ns dinsro.actions.accounts
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [expound.alpha :as expound]
            [dinsro.model.account :as m.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.actions.accounts :as s.a.accounts]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::s.accounts/name
   :initial-value ::s.accounts/initial-value})

(defn get-as-int
  [params key]
  (try
    (some-> params key str Integer/parseInt)
    (catch NumberFormatException e nil)))

(defn-spec prepare-record (s/nilable ::s.accounts/params)
  [params :create-account/params]
  (let [currency-id (get-as-int params :currency-id)
        user-id (get-as-int params :user-id)
        initial-value (some-> params :initial-value double)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::s.accounts/initial-value] initial-value)
                   (assoc-in [::s.accounts/currency :db/id] currency-id)
                   (assoc-in [::s.accounts/user :db/id] user-id))]
    (if (s/valid? ::s.accounts/params params)
      params
      (do (timbre/warnf "not valid: %s" (expound/expound-str ::s.accounts/params params))
          nil))))

(defn-spec create-handler ::s.a.accounts/create-handler-response
  [{:keys [params session]} ::s.a.accounts/create-handler-request]
  (or (let [user-id 1]
        (when-let [params (prepare-record params)]
          (let [id (m.accounts/create-record params #_(assoc params :user-id user-id))]
            (http/ok {:item (m.accounts/read-record id)}))))
      (http/bad-request {:status :invalid})))

(defn index-handler
  [request]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))

(defn read-handler
  [{{:keys [accountId]} :path-params}]
  (if-let [account (m.accounts/read-record {:id accountId})]
    (http/ok account)
    (http/not-found {})))

(defn-spec delete-handler ::s.a.accounts/delete-handler-response
  [{{:keys [id]} :path-params} ::s.a.accounts/delete-handler-request]
  (if-let [id (try (Integer/parseInt id) (catch NumberFormatException e))]
    (let [response (m.accounts/delete-record id)]
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))

(comment
  (create-handler {})
  (prepare-record {::s.accounts/name "foo"})
  (prepare-record (gen/generate (s/gen ::s.a.accounts/create-handler-request-valid)))
  (delete-handler {:path-params {:accountId "s"}})
  )
