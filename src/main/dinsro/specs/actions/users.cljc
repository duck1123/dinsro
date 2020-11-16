(ns dinsro.specs.actions.users
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(s/def ::create-params-valid (s/keys :req-un [::m.users/name]))
(def create-params-valid ::create-params-valid)

(s/def ::create-params (s/keys :opt-un [::m.users/name]))
(def create-params ::create-params)

;; Create

(s/def :create-user-request/params ::create-params)
(s/def ::create-request (s/keys :req-un [:create-user-request/params]))
(def create-request ::create-request)

(s/def :create-user-request/status (constantly 200))

(s/def ::create-response (s/keys :req-un [:create/status]))
(def create-response ::create-response)

;; Read

(s/def ::read-request ::ds/common-read-request)

(s/def :read-user-response/body (s/keys :req-un [::m.users/item]))
(s/def ::read-response-valid (s/keys :req-un [:read-user-response/body]))
(s/def ::read-response (s/or :not-found ::ds/common-response-not-found
                             :valid     ::read-response-valid))

(s/def ::index-request (s/keys))
(s/def ::index-response (s/keys))
