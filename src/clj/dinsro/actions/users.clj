(ns dinsro.actions.users
  (:require
   [buddy.hashers :as hashers]
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [dinsro.model.users :as m.users]
   [dinsro.spec.actions.users :as s.a.users]
   [dinsro.spec.users :as s.users]
   [expound.alpha :as expound]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name     ::s.users/name
   :email    ::s.users/email})

(defn prepare-record
  [params]
  (let [password-hash (some-> params ::s.users/password hashers/derive)
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))
        params (if (seq password-hash)
                 (assoc params ::s.users/password-hash password-hash)
                 params)]
    (if (s/valid? ::s.users/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.users/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.users/create-params)
  :ret (s/nilable ::s.users/params))

(defn create-handler
  [{:keys [params]}]
  (or (when-let [params (prepare-record params)]
        (if-let [id (m.users/create-record params)]
          (http/ok {:item (m.users/read-record id)})))
      (http/bad-request {:status :invalid})))

(defn delete-handler
  [request]
  (let [user-id (Integer/parseInt (:id (:path-params request)))]
    (m.users/delete-record user-id)
    (http/ok {:id user-id})))

(defn index-handler
  [_]
  (let [users (m.users/index-records)]
    (http/ok {:users users})))

(s/fdef index-handler
  :args (s/cat :request (s/keys))
  :ret (s/keys))

;; Read

(defn read-handler
  [request ]
  (let [{{id :id} :path-params} request]
    (if-let [id (try (Integer/parseInt id) (catch NumberFormatException _ nil))]
      (if-let [user (m.users/read-record id)]
        (http/ok {:item user})
        (http/not-found {:status :not-found}))
      (http/bad-request {:status :bad-request}))))

(s/fdef read-handler
  :args (s/cat :request ::s.a.users/read-request)
  :ret ::s.a.users/read-response)
