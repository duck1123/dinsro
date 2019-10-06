(ns dinsro.core
  (:require [ajax.core :as http]
            [day8.re-frame.http-fx]
            [dinsro.ajax :as ajax]
            [dinsro.components.login-page :as login-page]
            [dinsro.routing :as routing]
            [dinsro.state :as state]
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
   (kf/start!
    {:debug?         (boolean debug?)
     :routes         routing/routes
     :hash-routing?  true
     :initial-db     {::state/authenticated false
                      ::login-page/email    "foo@example.com"
                      ::login-page/password "hunter2"}
     :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
