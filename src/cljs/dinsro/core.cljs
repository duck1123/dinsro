(ns dinsro.core
  (:require [ajax.core :as http]
            [clojure.spec.alpha :as s]
            [day8.re-frame.http-fx]
            [dinsro.ajax :as ajax]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.routing :as routing]
            [dinsro.view :as view]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(kf/reg-event-db
 :status-loaded
 (fn [db [{:keys [identity]}]]
   (assoc db ::e.authentication/auth-id identity)))

(kf/reg-event-fx
 :status-errored
 (fn [_ _]
   (timbre/warn "status errored")))

(kf/reg-event-fx
 :init-status
 (fn [_ _]
   (timbre/info "init")
   {:http-xhrio
    {:uri "/api/v1/status"
     :method :get
     :response-format (http/json-response-format {:keywords? true})
     :on-success [:status-loaded]
     :on-failure [:status-errored]}}))

(kf/reg-controller
 :status-controller
 {:params (constantly true)
  :start [:init-status]})

(s/def ::failed boolean?)

(s/def ::db-spec
  (s/keys))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
   (rf/clear-subscription-cache!)
   (s/check-asserts true)
   (kf/start!
    {:debug?         false #_(boolean debug?)
     :routes         routing/routes
     :app-db-spec    ::db-spec
     :initial-db     {::e.debug/shown? false #_(boolean debug?)
                      ::e.transactions/items [e.transactions/example-transaction]}
     :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
