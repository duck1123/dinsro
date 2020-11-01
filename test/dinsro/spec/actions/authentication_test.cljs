(ns dinsro.spec.actions.authentication-test
  (:require
   [dinsro.cards :refer-macros [defcard]]
   [dinsro.spec :as ds]
   [dinsro.spec.actions.authentication :as s.a.authentication]
   [taoensso.timbre :as timbre]))

(defcard register-params
  (ds/gen-key ::s.a.authentication/register-params))

(defcard register-request
  (ds/gen-key ::s.a.authentication/register-request))

(defcard register-request-valid
  (ds/gen-key ::s.a.authentication/register-request-valid))

(defcard register-response
  (ds/gen-key ::s.a.authentication/register-response))


(defcard authenticate-request
  (ds/gen-key ::s.a.authentication/authenticate-request))

(defcard authenticate-response
  (ds/gen-key ::s.a.authentication/authenticate-response))
