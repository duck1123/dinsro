(ns dinsro.spec.actions.rates
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [dinsro.spec :as ds]
   [dinsro.spec.rates :as s.rates]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(s/def :dinsro.spec.actions.rates.lax/currency-id
  (s/or :id ::ds/id
        :zero zero?))
(s/def ::currency-id (s/or :id ::ds/id))
(def currency-id ::currency-id)

;; Create

(s/def ::date ::ds/date-string)
(s/def ::create-params (s/keys :opt-un [::s.rates/rate ::currency-id ::date]))
(s/def ::create-params-input (s/keys :req-un [::s.rates/rate :dinsro.spec.actions.rates.lax/currency-id ::date]))
(s/def ::create-params-valid (s/keys :req-un [::s.rates/rate ::currency-id ::date]))
(s/def :create-rates-request-valid/params ::create-params-valid)
(s/def ::create-request-valid (s/keys :req-un [:create-rates-request-valid/params]))
(def create-request-valid ::create-request-valid)

(s/def :create-rates-request/params ::create-params)
(s/def ::create-request (s/keys :req-un [:create-rates-request/params]))
(def create-request ::create-request)

(comment
  (ds/gen-key create-request-valid)
  (ds/gen-key create-request)
  )

(s/def :create-rates-response-valid/body (s/keys :req-un [::s.rates/item]))
(s/def :create-rates-response-valid/status #{status/ok})
(s/def ::create-response-valid (s/keys :req-un [:create-rates-response-valid/body
                                                        :create-rates-response-valid/status]))
(def create-response-valid ::create-response-valid)

(s/def ::create-response (s/or :invalid ::ds/common-response-invalid
                                       :valid   ::create-response-valid))
(def create-response ::create-response)

(comment
  (ds/gen-key create-response)
  )

;; Read

(s/def :read-rates-request/path-params (s/keys :req-un []))
(s/def ::read-request (s/keys :req-un [:read-rates-request/path-params]))
(def read-request ::read-request)

(s/def :read-rates-response/body (s/keys :req-un [::s.rates/item]))
(s/def ::read-response-valid (s/keys :req-un [:read-rates-response/body]))
(def read-response-valid ::read-response-valid)

(s/def ::read-response (s/or :not-found ::ds/common-response-not-found
                             :valid     ::read-response-valid))
(def read-response ::read-response)

;; Delete

(s/def :delete-rates-request-params/id (s/with-gen string? #(gen/fmap str (s/gen pos-int?))))
(s/def :delete-rates-request/path-params (s/keys :req-un [:delete-rates-request-params/id]))
(s/def ::delete-request (s/keys :req-un [:delete-rates-request/path-params]))
(def delete-request ::delete-request)

(s/def ::delete-response (s/keys))
(def delete-response ::delete-response)

;; Index

(s/def ::index-request (s/keys))
(def index-request ::index-request)

(s/def :index-rates-response/items (s/coll-of ::s.rates/item))
(s/def :index-rates-response/body (s/keys :req-un [:index-rates-response/items]))
(s/def ::index-response (s/keys :req-un [:index-rates-response/body]))
(def index-response ::index-response)

;; Index by Currency

(s/def ::index-by-currency-request (s/keys :req-un [:common-request-show/path-params]))
(def index-by-currency-request ::index-by-currency-request)

(s/def :index-rates-by-currency-response-body/items ::s.rates/rate-feed)
(s/def :index-rates-by-currency-response-body/currency-id ds/id)
(s/def :index-rates-by-currency-response/body
  (s/keys :req-un [:index-rates-by-currency-response-body/currency-id
                   :index-rates-by-currency-response-body/items]))
(s/def :index-rates-by-currency-response-valid/status #{status/ok})
(s/def ::index-by-currency-response (s/keys :req-un [:index-rates-by-currency-response/body
                                                     :index-rates-by-currency-response-valid/status]))
(def index-by-currency-response ::index-by-currency-response)
