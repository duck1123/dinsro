(ns dinsro.actions.admin-categories
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
  [:create-category/params => (? ::m.categories/params)]
  (let [user-id (utils/get-as-int params :user-id)
        params  (-> params
                    (set/rename-keys param-rename-map)
                    (select-keys (vals param-rename-map))
                    (assoc-in [::m.categories/user :db/id] user-id))]
    (if (s/valid? ::m.categories/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.categories/params params)))
        nil))))

(>defn create-handler
  [{:keys [params]}]
  [::s.a.categories/create-request => ::s.a.categories/create-response]
  (or
   (when-let [params (prepare-record params)]
     (let [id (q.categories/create-record params)]
       (http/ok {:item (q.categories/read-record id)})))
   (http/bad-request {:status :invalid})))

(>defn index-handler
  [_]
  [(s/keys) => (s/keys)]
  (let [categories (q.categories/index-records)]
    (http/ok {:items categories})))

(>defn read-handler
  [{{:keys [id]} :path-params}]
  [::s.a.categories/read-request => ::s.a.categories/read-response]
  (if-let [category (q.categories/read-record (utils/try-parse-int id))]
    (http/ok category)
    (http/not-found {:status :not-found})))

(>defn delete-handler
  [{{:keys [id]} :path-params}]
  [::s.a.categories/delete-request => ::s.a.categories/delete-response]
  (if-let [id (utils/try-parse-int id)]
    (do
      (q.categories/delete-record id)
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))
