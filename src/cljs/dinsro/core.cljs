(ns dinsro.core
  (:require [clojure.spec.alpha :as s]
            [day8.re-frame.http-fx]
            [dinsro.ajax :as ajax]
            [dinsro.events.debug :as e.debug]
            [dinsro.routing :as routing]
            [dinsro.view :as view]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn initial-db
  [debug?]
  {::e.debug/shown?                                      debug?
   ::e.debug/enabled?                                    debug?
   :dinsro.spec.events.forms.settings/allow-registration true})

(s/def ::app-db (s/keys))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
   (rf/clear-subscription-cache!)

   (s/check-asserts (boolean debug?))

   (kf/start!
    {:debug?         (boolean debug?)
     :routes         routing/routes
     :app-db-spec    ::app-db
     :initial-db     (initial-db (boolean? debug?))
     :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
