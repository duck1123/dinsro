(ns dinsro.core
  (:require [ajax.core :as http]
            [day8.re-frame.http-fx]
            [dinsro.ajax :as ajax]
            [dinsro.routing :as routing]
            [dinsro.view :as view]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-event-db
 :status-loaded
 (fn [db [_ {:keys [identity]}]]
   (assoc db :authenticate identity)))

(rf/reg-event-fx
 :status-errored
 (fn [_ _]
   (timbre/warn "status errored")))

(rf/reg-event-fx
 :init-status
 (fn
  [_ _]
   (timbre/info "init")
   {:http-xhrio {:uri "/api/v1/status"
                 :method :get
                 :response-format (http/json-response-format {:keywords? true})
                 :on-success [:status-loaded]
                 :on-failure [:status-errored]}}))

(kf/reg-controller
 :status-controller
 {:params (constantly true)
  :start [:init-status]})

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
   (rf/clear-subscription-cache!)
   (kf/start!
    {:debug?         (boolean debug?)
     :routes         routing/routes
     ;; :hash-routing?  false
     :initial-db     {}
     :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
