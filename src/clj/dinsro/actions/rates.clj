(ns dinsro.actions.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.rates :as s.rates]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :create-rates-valid/params (s/keys :req-un [::s.rates/name]))
(s/def :create-rates/params (s/keys :opt-un [::s.rates/name]))
(s/def :create-rates-response/body (s/keys))

(s/def ::create-handler-request (s/keys :req-un [:create-handler/params]))
(s/def ::create-handler-response (s/keys :req-un [:create-rates-response/body]))
(s/def ::index-handler-request (s/keys))
(s/def ::index-handler-response (s/keys))

(comment
  (gen/generate (s/gen ::create-handler-request))
  (gen/generate (s/gen ::create-handler-response))
  )


(defn-spec index-handler ::index-handler-response
  [request ::index-handler-request]
  (let [items (m.rates/index-records)]
    (http/ok {:model :rates :items items})))

(defn-spec create-handler ::create-handler-response
  [request ::create-handler-request]
  (http/ok {:status "ok"}))
