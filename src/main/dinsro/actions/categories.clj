(ns dinsro.actions.categories
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [expound.alpha :as expound]
   [dinsro.queries.categories :as q.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs.actions.categories :as s.a.categories]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name ::m.categories/name})

(>defn prepare-record
  [params]
  [::s.a.categories/create-params => (? ::m.categories/params)]
  (let [user-id (utils/get-as-int params :user-id)
        params  (-> params
                    (set/rename-keys param-rename-map)
                    (select-keys (vals param-rename-map))
                    (assoc-in [::m.categories/user :db/id] user-id))]
    (if (s/valid? ::m.categories/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::m.categories/params params)))
        nil))))

(>defn create!
  [params]
  [::s.a.categories/create-params => (? ::m.categories/item)]
  (some-> params q.categories/create-record q.categories/read-record))

(>defn create-handler
  [{:keys [params]}]
  [::s.a.categories/create-request => ::s.a.categories/create-response]
  (or (when-let [item (some-> params prepare-record create!)]
        (http/ok {:item item}))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [{{:keys [id]} :path-params}]
  [::s.a.categories/read-request => ::s.a.categories/read-response]
  (if-let [id (utils/try-parse-int id)]
    (if-let [category (q.categories/read-record id)]
      (http/ok category)
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :invalid})))

(>defn delete-handler
  [{{:keys [id]} :path-params}]
  [::s.a.categories/delete-request => ::s.a.categories/delete-response]
  (if-let [id (utils/try-parse-int id)]
    (do
      (q.categories/delete-record id)
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))

(>defn index-handler
  [_]
  [::s.a.categories/index-request => ::s.a.categories/index-response]
  (let [categories (q.categories/index-records)]
    (http/ok {:items categories})))
