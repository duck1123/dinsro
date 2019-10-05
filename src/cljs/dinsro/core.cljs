(ns dinsro.core
  (:require [ajax.core :as http]
            [dinsro.ajax :as ajax]
            [dinsro.routing :as routing]
            [dinsro.view :as view]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]))

(rf/reg-event-fx
  ::load-about-page
  (constantly nil))

(kf/reg-controller
  ::about-controller
  {:params (constantly true)
   :start  [::load-about-page]})

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
    (rf/clear-subscription-cache!)
    (kf/start! {:debug?         (boolean debug?)
                :routes         routing/routes
                :hash-routing?  true
                :initial-db     {}
                :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
