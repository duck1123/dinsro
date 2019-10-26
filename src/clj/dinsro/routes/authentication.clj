(ns dinsro.routes.authentication
  (:require [dinsro.actions.authentication :refer [authenticate register]]
            [dinsro.layout :as layout]
            [taoensso.timbre :as timbre]))

(defn authenticate-handler
  [request]
  (let [{:keys [authentication-data]} request]
    (authenticate (assoc request :authentication-data authentication-data))))

(defn authentication-routes []
  (list
   ["/authenticate" {:post {:handler authenticate-handler
                            :summary "Authenticate"}}]
   ["/register" {:post register}]))
