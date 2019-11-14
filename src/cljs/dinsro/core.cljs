(ns dinsro.core
  (:require [ajax.core :as http]
            [day8.re-frame.http-fx]
            [dinsro.ajax :as ajax]
            [dinsro.views.login :as login-page]
            [dinsro.routing :as routing]
            [dinsro.state :as state]
            [dinsro.view :as view]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]))

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
     :initial-db     {::state/authenticated false}
     :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
