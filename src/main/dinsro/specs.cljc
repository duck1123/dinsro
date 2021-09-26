(ns dinsro.specs
  (:refer-clojure :exclude [instance?])
  (:require
   [clojure.core.async]
   [clojure.spec.alpha :as s]
   [clojure.test.check.generators]
   [clojure.spec.gen.alpha :as gen]
   [ring.util.http-status :as status]
   [tick.alpha.api :as tick]
   [time-specs.core :as ts])
  (:import
   clojure.core.async.impl.channels.ManyToManyChannel))

(defn valid-jwt? [jwt]
  (re-matches #"^[a-zA-Z0-9\-_]+?\.[a-zA-Z0-9\-_]+?\.([a-zA-Z0-9\-_]+)?$" jwt))

(defn uuid-str-gen []
  (gen/fmap str (s/gen uuid?)))

(defn gen-key
  [key]
  (gen/generate (s/gen key)))

(defn instance?
  [c]
  (partial clojure.core/instance? c))

(def chan? (instance? ManyToManyChannel))

(def non-empty-string-alphanumeric
  "Generator for non-empty alphanumeric strings"
  (gen/such-that #(not= "" %) (gen/string-alphanumeric)))

(s/def ::id uuid?)
(s/def :db/id ::id)
(def id ::id)

(s/def ::valid-double (s/and double? #(== % %)))
(def valid-double ::valid-double)

(s/def ::json-doubleable (s/or :int int?
                               :double ::valid-double))
(def json-doubleable ::json-doubleable)

(s/def ::double-string (s/with-gen string? #(gen/fmap str (s/gen ::valid-double))))
(def double-string ::double-string)

(s/def ::date-string (s/with-gen string? #(s/gen #{(str (tick/instant))})))
(def date-string ::date-string)

(s/def ::date (s/with-gen ts/instant? #(gen/fmap tick/instant (s/gen ::date-string))))
(def date ::date)

(s/def ::id-string (s/with-gen (s/and string? #(re-matches #"\d+" %))
                     #(gen/fmap str (s/gen pos-int?))))
(def id-string ::id-string)

(s/def ::id-string-opt (s/with-gen (s/and string? #(re-matches #"\d*" %))
                         #(gen/fmap str (s/gen pos-int?))))
(def id-string-opt ::id-string-opt)

(s/def ::not-found-status #{:not-found})
(def not-found-status ::not-found-status)

(s/def ::invalid-status #{:invalid})
(def invalid-status ::invalid-status)

(s/def :common-request-show-path-params/id ::id-string)
(s/def :common-request-show/path-params (s/keys :req-un [:common-request-show-path-params/id]))
(s/def ::common-read-request (s/keys :req-un [:common-request-show/path-params]))
(def common-read-request ::common-read-request)

(s/def :common-response-invalid-body/status ::invalid-status)
(s/def :common-response-invalid/body (s/keys :req-un [:common-response-invalid-body/status]))
(s/def :common-response-invalid/status #{status/bad-request})
(s/def ::common-response-invalid (s/keys :req-un [:common-response-invalid/body
                                                  :common-response-invalid/status]))
(def common-response-invalid ::common-response-invalid)

(s/def :common-response-not-found-body/status ::not-found-status)
(s/def :common-response-not-found/body (s/keys :req-un [:common-response-not-found-body/status]))
(s/def ::common-response-not-found (s/keys :req-un [:common-response-not-found/body]))
(def common-response-not-found ::common-response-not-found)

(s/def ::state #{:failed :loading :loaded :invalid})
(def state ::state)
