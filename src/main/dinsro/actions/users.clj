(ns dinsro.actions.users
  (:require
   [buddy.hashers :as hashers]
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [dinsro.specs.actions.users :as s.a.users]
   [expound.alpha :as expound]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name     ::m.users/name
   :email    ::m.users/email})

(defn prepare-record
  [params]
  (let [password-hash (some-> params ::m.users/password hashers/derive)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))
        params (if (seq password-hash)
                 (assoc params ::m.users/password-hash password-hash)
                 params)]
    (if (s/valid? ::m.users/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::m.users/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.users/create-params)
  :ret (s/nilable ::m.users/params))

(defn create-handler
  [{:keys [params]}]
  (or (when-let [params (prepare-record params)]
        (if-let [id (q.users/create-record params)]
          (http/ok {:item (q.users/read-record id)})))
      (http/bad-request {:status :invalid})))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:id (:path-params request)))]
    (q.users/delete-record user-id)
    (http/ok {:id user-id})))

(defn index-handler
  [_]
  (let [users (q.users/index-records)]
    (http/ok {:items users})))

(s/fdef index-handler
  :args (s/cat :request (s/keys))
  :ret (s/keys))

;; Read

(defn read-handler
  [request ]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
      (if-let [user (q.users/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))

(s/fdef read-handler
  :args (s/cat :request ::s.a.users/read-request)
  :ret ::s.a.users/read-response)
