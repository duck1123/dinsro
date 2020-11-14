(ns dinsro.actions.admin-categories
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.queries.categories :as q.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs.actions.categories :as s.a.categories]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::m.categories/name})

(defn prepare-record
  [params]
  (let [user-id (utils/get-as-int params :user-id)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::m.categories/user :db/id] user-id))]
    (if (s/valid? ::m.categories/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.categories/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params :create-category/params)
  :ret (s/nilable ::m.categories/params))

(defn create-handler
  [{:keys [params]}]
  (or
   (when-let [params (prepare-record params)]
     (let [id (q.categories/create-record params)]
       (http/ok {:item (q.categories/read-record id)})))
   (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.categories/create-request)
  :ret ::s.a.categories/create-response)

(defn index-handler
  [_]
  (let [categories (q.categories/index-records)]
    (http/ok {:items categories})))

(s/fdef index-handler
  :args (s/cat :request (s/keys))
  :ret (s/keys))

(defn read-handler
  [{{:keys [id]} :path-params}]
  (if-let [category (q.categories/read-record (utils/try-parse-int id))]
    (http/ok category)
    (http/not-found {:status :not-found})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.categories/read-request)
  :ret ::s.a.categories/read-response)

(defn delete-handler
  [{{:keys [id]} :path-params}]
  (if-let [id (utils/try-parse-int id)]
    (do
      (q.categories/delete-record id)
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.categories/delete-request)
  :ret ::s.a.categories/delete-response)
