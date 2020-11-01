(ns dinsro.actions.categories
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs.categories :as s.categories]
   [dinsro.specs.actions.categories :as s.a.categories]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name          ::s.categories/name})

;; Prepare

(defn prepare-record
  [params]
  (let [user-id (utils/get-as-int params :user-id)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map))
                   (assoc-in [::s.categories/user :db/id] user-id))]
    (if (s/valid? ::s.categories/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.categories/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.categories/create-params)
  :ret  (s/nilable ::s.categories/params))

;; Create

(defn create-handler
  [{:keys [params]}]
  (or (when-let [params (prepare-record params)]
        (let [id (m.categories/create-record params)]
          (http/ok {:item (m.categories/read-record id)})))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.categories/create-request)
  :ret ::s.a.categories/create-response)

;; Read

(defn read-handler
  [{{:keys [id]} :path-params}]
  (if-let [id (utils/try-parse-int id)]
    (if-let [category (m.categories/read-record id)]
      (http/ok category)
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :invalid})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.categories/read-request)
  :ret ::s.a.categories/read-response)

;; Delete

(defn delete-handler
  [{{:keys [id]} :path-params}]
  (if-let [id (utils/try-parse-int id)]
    (do
      (m.categories/delete-record id)
      (http/ok {:status "ok"}))
    (http/bad-request {:input :invalid})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.categories/delete-request)
  :ret ::s.a.categories/delete-response)

;; Index

(defn index-handler
  [_]
  (let [categories (m.categories/index-records)]
    (http/ok {:items categories})))

(s/fdef index-handler
  :args (s/cat :request ::s.a.categories/index-request)
  :ret ::s.a.categories/index-response)
