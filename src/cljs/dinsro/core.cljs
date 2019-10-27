(ns dinsro.core
  (:require [ajax.core :as http]
            [clojure.spec.alpha :as s]
            [day8.re-frame.http-fx]
            [dinsro.ajax :as ajax]
            [dinsro.components.forms.registration-form :as c.rf]
            [dinsro.events.rates :as e.rates]
            [dinsro.events.users :as e.users]
            [dinsro.routing :as routing]
            [dinsro.specs :as ds]
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

(s/def ::failed boolean?)

(s/def ::db-spec
  (s/keys
   :req [
         #_::ds/new-password

         ]

   :req-un [ ]
   :opt [
         ::e.rates/items
         ::e.users/item
         ::c.rf/name
         ]
   :opt-un [::failed
            :dinsro.events.users/item
            :kee-frame/route]))

;; ::c.registration-form/name

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
   (rf/clear-subscription-cache!)
   (s/check-asserts true)
   (kf/start!
    {:debug?         (boolean debug?)
     :routes         routing/routes
     ;; :hash-routing?  false
     :app-db-spec    ::db-spec
     :initial-db     {}
     :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
