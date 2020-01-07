(ns dinsro.events.authentication
  (:require
   [ajax.core :as ajax]
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.spec.actions.authentication :as s.a.authentication]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(c/reg-field ::auth-id nil)
(s/def ::auth-id (s/nilable :db/id))
(def auth-id ::auth-id)

;; Authenticate

(defn do-authenticate-success
  [cofx event]
  (let [{:keys [db]} cofx
        [item] event
        identity (::s.a.authentication/identity item)
        return-to (:return-to db)
        db (if return-to
             (-> db
                 (assoc :kee-frame/route return-to)
                 (dissoc :return-to))
             db)]
    {
     ;; TODO: return to calling page
     :navigate-to [:home-page]
     :db (assoc db ::auth-id identity)}))

(defn do-authenticate-failure
  [_ _]
  {})

(defn do-authenticate
  [_ [data _]]
  {:http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-authenticate])
    :params          data
    :timeout         8000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-authenticate-success]
    :on-failure      [::do-authenticate-failure]}})

(kf/reg-event-fx ::do-authenticate-success do-authenticate-success)
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
  {:http-xhrio
   {:uri             (kf/path-for [:api-logout])
    :method          :post
    :on-success      [::do-logout-success]
    :on-failure      [::do-logout-failure]
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}})

(kf/reg-event-fx ::do-logout-success do-logout-success)
(kf/reg-event-fx ::do-logout-failure do-logout-failure)
(kf/reg-event-fx ::do-logout do-logout)

;; Register

(defn register-succeeded
  [_ _]
  {})

(defn register-failed
  [_ _]
  {})

(defn submit-clicked
  [_ [form-data]]
  {:http-xhrio
   {:uri             "/api/v1/register"
    :method          :post
    :timeout         8000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :params          form-data
    :on-success      [:register-succeeded]
    :on-failure      [:register-failed]}})

(kf/reg-event-fx :register-succeeded register-succeeded)
(kf/reg-event-fx :register-failed register-failed)
(kf/reg-event-fx ::submit-clicked submit-clicked)
