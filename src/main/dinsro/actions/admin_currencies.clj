(ns dinsro.actions.admin-currencies
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs.actions.admin-currencies :as s.a.admin-currencies]
   [expound.alpha :as expound]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name ::m.currencies/name})

;; Prepare

(defn prepare-record
  [params]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (if (s/valid? ::m.currencies/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.currencies/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.admin-currencies/create-params)
  :ret  (s/nilable ::m.currencies/params))

;; Create

(defn create-handler
  [request]
  (or (let [{:keys [params]} request]
        (when-let [params (prepare-record params)]
          (let [id (q.currencies/create-record params)]
            (http/ok {:item (q.currencies/read-record id)}))))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.admin-currencies/create-request)
  :ret ::s.a.admin-currencies/create-response)

;; Read

(defn read-handler
  [request]
  (let [id (some-> request :path-params :id Integer/parseInt)]
    (if-let [item (q.currencies/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))))

(s/fdef read-handler
  :args (s/cat :request ::s.a.admin-currencies/read-request)
  :ret ::s.a.admin-currencies/read-response)

;; Delete

(defn delete-handler
  [request]
  (let [{{:keys [id]} :path-params} request]
    (or (try
          (let [id (Integer/parseInt id)]
            (q.currencies/delete-record id)
            (http/ok {:id id}))
          (catch NumberFormatException _ nil))
        (http/bad-request {:status :invalid}))))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.admin-currencies/delete-request)
  :ret ::s.a.admin-currencies/delete-response)

;; Index

(defn index-handler
  [_request]
  (let [items (q.currencies/index-records)]
    (http/ok {:items items})))

(s/fdef index-handler
  :args (s/cat :request ::s.a.admin-currencies/index-request)
  :ret ::s.a.admin-currencies/index-response)
