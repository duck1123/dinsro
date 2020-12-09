(ns dinsro.events.authentication
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.specs.actions.authentication :as s.a.authentication]
   [dinsro.specs.events.forms.registration :as s.e.f.registration]
   [dinsro.store :as st]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(s/def ::auth-id (s/nilable :db/id))
(def auth-id ::auth-id)

;; Authenticate

(defn do-authenticate-success
  [{:keys [db]} [item]]
  (let [identity (::s.a.authentication/identity item)
        token (:token item)
        return-to (:return-to db)
        db (if return-to
             (-> db
                 (assoc :kee-frame/route return-to)
                 (dissoc :return-to))
             db)]
    {:cookie/set {:name "token" :value token}
     :db (assoc (assoc db ::auth-id identity) :token token)
     ;; TODO: return to calling page
     :navigate-to [:home-page]}))

(defn do-authenticate-failure
  [{:keys [db]} [{{:keys [message]
                   :or {message "LoginFailed"}} :response}]]
  {:db (assoc db ::error-message message)})

(defn do-authenticate
  [store _cofx [data _]]
  {:http-xhrio
   (e/post-request
    [:api-authenticate]
    store
    [::do-authenticate-success]
    [::do-authenticate-failure]
    data)})

;; Logout

(defn do-logout-success
  [{:keys [db]} _]
  {:db (assoc db ::auth-id nil)
   :navigate-to [:login-page]})

;; You failed to logout. logout anyway
(defn do-logout-failure
  [{:keys [db]} _]
  {:db (assoc db ::auth-id nil)
   :navigate-to [:login-page]})

(defn do-logout
  [store _ _]
  {:cookie/remove {:name "token"}
   :http-xhrio
   (e/post-request
    [:api-logout]
    store
    [::do-logout-success]
    [::do-logout-failure]
    nil)})

;; Register

(defn register-succeeded
  [{:keys [db]} _]
  {:db (assoc db ::s.e.f.registration/error-message "")})

(defn register-failed
  [{:keys [db]} [{{:keys [message]} :response}]]
  (let [error-message (or message "Registration Failed")]
    {:db (assoc db ::s.e.f.registration/error-message error-message)}))

(defn submit-registration
  [store {:keys [db]} [data]]
  {:db (assoc db ::s.e.f.registration/error-message "")
   :http-xhrio
   (e/post-request
    [:api-register]
    store
    [:register-succeeded]
    [:register-failed]
    data)})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::auth-id)
    (st/reg-basic-sub ::error-message)
    (st/reg-set-event ::auth-id)
    (st/reg-event-fx ::do-authenticate-success
                     [(rf/inject-cofx :cookie/get [:token])] do-authenticate-success)
    (st/reg-event-fx ::do-authenticate-failure do-authenticate-failure)
    (st/reg-event-fx ::do-authenticate (partial do-authenticate store))
    (st/reg-event-fx ::do-logout-success do-logout-success)
    (st/reg-event-fx ::do-logout-failure do-logout-failure)
    (st/reg-event-fx ::do-logout (partial do-logout store))
    (st/reg-event-fx :register-succeeded register-succeeded)
    (st/reg-event-fx :register-failed register-failed)
    (st/reg-event-fx ::submit-registration (partial submit-registration store)))

  store)
