(ns dinsro.events.authentication
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::auth-id nil)
(s/def ::auth-id (s/nilable string?))

(c/reg-field ::loading false)
(s/def ::loading boolean?)

(c/reg-field ::login-failed false)
(s/def ::login-failed boolean?)

;; Authenticate

(defn do-authenticate-success
  [cofx event]
  (let [{:keys [db]} cofx
        [{:keys [identity]}] event
        return-to (:return-to db)
        db (if return-to
             (-> db
                 (assoc :kee-frame/route return-to)
                 (dissoc :return-to))
             db)]
    {:db (-> db
             (assoc ::auth-id identity)
             (assoc ::loading false)
             (assoc ::login-failed false))}))

(defn do-authenticate-failure
  [{:keys [db]} _]
  {:db  (-> db
            (assoc ::login-failed true)
            (assoc ::loading false))})

(defn do-authenticate
  [cofx event]
  (let [{:keys [db]} cofx
        [data return-to] event]
    {:db (assoc db ::loading true)
     :http-xhrio
     {:method          :post
      :uri             (kf/path-for [:api-authenticate])
      :params          data
      :timeout         8000
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :on-success      [::do-authenticate-success]
      :on-failure      [::do-authenticate-failure]}}))

(kf/reg-event-fx ::do-authenticate-success do-authenticate-success)
(kf/reg-event-fx ::do-authenticate-failure do-authenticate-failure)
(kf/reg-event-fx ::do-authenticate do-authenticate)

;; Logout

(defn do-logout-success
  [db _]
  (assoc db ::auth-id nil))

;; You failed to logout. logout anyway
(defn do-logout-failure
  [db _]
  (assoc db ::auth-id nil))

(defn do-logout
  [_ _]
  {:http-xhrio
   {:uri             (kf/path-for [:api-logout])
    :method          :post
    :on-success      [::do-logout-success]
    :on-failure      [::do-logout-failure]
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}})

(kf/reg-event-db ::do-logout-success do-logout-success)
(kf/reg-event-db ::do-logout-failure do-logout-failure)
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
