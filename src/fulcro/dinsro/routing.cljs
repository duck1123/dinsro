(ns dinsro.routing
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]]
   [dinsro.app :as da]
   [pushy.core :as pushy]
   [taoensso.timbre :as timbre]))

(defonce history
  (pushy/pushy
   (fn [p]
     (let [route-segments (vec (rest (string/split p "/")))]
       (dr/change-route da/app route-segments)))
   identity))

(defn route-to!
  [route-string]
  (pushy/set-token! history route-string))

(defmutation finish-login [_]
  (action
   [{:keys [_app state]}]
   (let [logged-in? (get-in @state [:session/current-user :user/valid])]
     (when-not logged-in?
       (route-to! "/login"))
     (swap! state #(assoc % :root/ready? true)))))

(defn start!
  []
  (dr/initialize! da/app)
  (pushy/start! history))
