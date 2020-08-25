(ns dinsro.events.authentication
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.components :as c]
   [dinsro.spec.actions.authentication :as s.a.authentication]
   [dinsro.spec.events.forms.registration :as s.e.f.registration]
   [kee-frame.core :as kf]
   [re-frame.core :as r]
   [taoensso.timbre :as timbre]))

(c/reg-field ::auth-id nil)
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
    {:cookie/set {:name "token"
                  :value token}
     :db (assoc (assoc db ::auth-id identity) :token token)
     ;; TODO: return to calling page
     :navigate-to [:home-page]}))

(defn do-authenticate-failure
  [_ _]
  {})

(defn do-authenticate
  [_ [data _]]
  {:http-xhrio
   (e/post-request
    [:api-authenticate]
    [::do-authenticate-success]
    [::do-authenticate-failure]
    data)})

(kf/reg-event-fx
 ::do-authenticate-success
 [(r/inject-cofx :cookie/get [:token])]
 do-authenticate-success)
(kf/reg-event-fx ::do-authenticate-failure do-authenticate-failure)
(kf/reg-event-fx ::do-authenticate do-authenticate)

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
  [_ _]
  {:cookie/remove {:name "token"}
   :http-xhrio
   (e/post-request
    [:api-logout]
    [::do-logout-success]
    [::do-logout-failure]
    nil)})

(kf/reg-event-fx ::do-logout-success do-logout-success)
(kf/reg-event-fx ::do-logout-failure do-logout-failure)
(kf/reg-event-fx ::do-logout do-logout)

;; Register

(defn register-succeeded
  [{:keys [db]} _]
  {:db (assoc db ::s.e.f.registration/error-message "")})

(defn register-failed
  [{:keys [db]} [{{:keys [message]} :response}]]
  (let [error-message (or message "Registration Failed")]
    {:db (assoc db ::s.e.f.registration/error-message error-message)}))

(defn submit-registration
  [{:keys [db]} [data]]
  {:db (assoc db ::s.e.f.registration/error-message "")
   :http-xhrio
   (e/post-request
    [:api-register]
    [:register-succeeded]
    [:register-failed]
    data)})

(kf/reg-event-fx :register-succeeded register-succeeded)
(kf/reg-event-fx :register-failed register-failed)
(kf/reg-event-fx ::submit-registration submit-registration)
