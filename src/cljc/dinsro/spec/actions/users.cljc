(ns dinsro.actions.users
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def :create-user-request/params (s/keys :opt [::s.users/name]))
(s/def ::create-request (s/keys :req-un [:create-request/params]))
(s/def :create-user-request/status (constantly 200))
(s/def ::create-response (s/keys :req-un [:create-handler/status]))

(s/def :read-user-request/id string?)
(s/def :read-user-request/path-params (s/keys :req-un [:read-user-request/id]))
(s/def ::read-handler-request-valid (s/keys :req-un [:read-user-request/path-params]))
(s/def ::read-handler-request (s/keys :req-un [:read-user-request/path-params]))

(s/def :read-user-response/body (s/keys :req-un [::s.users/item]))
(s/def :read-user-response-not-found-body/status #{:not-found})
(s/def :read-user-response-not-found/body (s/keys :req-un [:read-user-response-not-found-body/status]))
(s/def ::read-handler-response-valid (s/keys :req-un [:read-user-response/body]))
(s/def ::read-handler-response-not-found (s/keys :req-un [:read-user-response-not-found/body]))
(s/def ::read-handler-response (s/or :not-found ::read-handler-response-not-found
                                     :valid     ::read-handler-response-valid))

(comment
  ;; (gen/generate (s/gen :read-rates-request/path-params))
  (gen/generate (s/gen ::read-handler-request))
  (gen/generate (s/gen ::read-handler-request-valid))
  )
