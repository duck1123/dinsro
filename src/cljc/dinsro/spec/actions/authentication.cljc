(ns dinsro.spec.actions.authentication
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.users :as s.users]))

(s/def :register-optional/params
  (s/keys :opt-un [::s.users/name ::s.users/email ::s.users/password]))
(s/def :register/params
  (s/keys :req-un [::s.users/name ::s.users/email ::s.users/password]))

(s/def ::register-request (s/keys :req-un [:register-optional/params]))
(s/def ::register-request-valid (s/keys :req-un [:register/params]))

(s/def :register/body any?)
(s/def :register/request (s/keys :req-un [:register/body]))
(s/def ::register-response (s/keys :req-un [:register/body]))

(s/def ::authenticate-request (s/keys))
(s/def ::authenticate-response (s/keys))
