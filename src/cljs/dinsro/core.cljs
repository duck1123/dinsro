(ns dinsro.core
  (:require
   [clojure.spec.alpha :as s]
   [com.smxemail.re-frame-cookie-fx]
   [day8.re-frame.http-fx]
   [dinsro.ajax :as ajax]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.settings :as c.settings]
   [dinsro.components.status :as c.status]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.websocket]
   [dinsro.routing :as routing]
   [dinsro.view :as view]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(def ^:dynamic *debug* false)

(defn initial-db
  [debug?]
  {::e.debug/shown?                                      debug?
   :token                                                nil
   ::e.debug/enabled?                                    debug?
   :dinsro.spec.events.forms.settings/allow-registration true})

(s/def ::app-db (s/keys))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  []
  (rf/clear-subscription-cache!)
  (s/check-asserts (boolean *debug*))

  (kf/start!
   {:debug?         *debug*
    :routes         routing/routes
    :app-db-spec    ::app-db
    :initial-db     (initial-db *debug*)
    :root-component [(fn []
                       [error-boundary
                        [c.status/require-status
                         (c.settings/require-settings
                          [view/root-component])]])]}))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (binding [*debug* debug?]
    (mount-components)))
