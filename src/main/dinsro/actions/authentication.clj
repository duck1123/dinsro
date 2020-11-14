(ns dinsro.actions.authentication
  (:require
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]
   [clojure.spec.alpha :as s]
   [dinsro.actions.users :as a.users]
   [dinsro.config :refer [secret]]
   [dinsro.queries.users :as q.users]
   [dinsro.model.users :as m.users]
   [dinsro.specs.actions.authentication :as s.a.authentication]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(defn authenticate-handler
  [request]
  (let [{{:keys [email password]} :params} request]
    (if (and (seq email) (seq password))
      (if-let [user (q.users/find-by-email email)]
        (if-let [password-hash (::m.users/password-hash user)]
          (if (hashers/check password password-hash)
            (let [id (:db/id user)
                  claims {:user id
                          :exp (time/plus (time/now) (time/minutes 3600))}]
              (-> {::s.a.authentication/identity id
                   :token (jwt/sign claims secret)}
                  (http/ok)
                  (assoc-in [:session :identity] id)))
            ;; Password does not match
            (http/unauthorized {:status :unauthorized}))
          ;; No password, invalid user
          (http/unauthorized {:status :unauthorized}))
        ;; User not found
        (http/unauthorized {:status :unauthorized}))
      (http/bad-request {:status :invalid}))))

(s/fdef authenticate-handler
  :args (s/cat :request ::s.a.authentication/authenticate-request)
  :ret ::s.a.authentication/authenticate-response)

(defn register-handler
  "Register a user"
  [request]
  (let [{:keys [params]} request
        params (if-let [password (:password params)]
                 (assoc params ::m.users/password password)
                 params)
        params (dissoc params :password)
        params (a.users/prepare-record params)]
    (if (s/valid? ::m.users/params params)
      (try
        (let [id (q.users/create-record params)]
          (http/ok {:id id}))
        (catch RuntimeException _
          (http/bad-request {:status :failed
                             :message "User already exists"})))
      (http/bad-request {:status :failed
                         :message "Invalid"}))))

(s/fdef register-handler
  :args (s/cat :request ::s.a.authentication/register-request)
  :ret ::s.a.authentication/register-response)

(defn logout-handler
  [_]
  (assoc-in (http/ok {:identity nil}) [:session :identity] nil))
