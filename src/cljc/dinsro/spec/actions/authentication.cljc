(ns dinsro.spec.actions.authentication
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.users :as s.users]))

(s/def :register-handler-optional/params
  (s/keys :opt-un [::s.users/name ::s.users/email ::s.users/password]))
(s/def :register-handler/params
  (s/keys :req-un [::s.users/name ::s.users/email ::s.users/password]))

(s/def ::register-request (s/keys :req-un [:register-handler-optional/params]))
(s/def ::register-request-valid (s/keys :req-un [:register-handler/params]))

(s/def :register-handler/body any?)
(s/def :register-handler/request (s/keys :req-un [:register-handler/body]))
(s/def ::register-handler-response (s/keys :req-un [:register-handler/body]))

(s/def ::authenticate-handler-request (s/keys))
