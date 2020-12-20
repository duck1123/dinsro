(ns dinsro.routing
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.app :as da]
   [pushy.core :as pushy]
   [taoensso.timbre :as timbre]))

(defonce history
  (pushy/pushy
   (fn [p]
     (let [route-segments (vec (rest (string/split p "/")))]
       (dr/change-route! da/app route-segments)))
   identity))

(defn route-to!
  [route-string]
  (pushy/set-token! history route-string))

(defn start!
  []
  (dr/initialize! da/app)
  (pushy/start! history))
