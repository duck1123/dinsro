(ns dinsro.spec.events.accounts
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.accounts :as s.accounts]
            [taoensso.timbre :as timbre]))

(s/def ::item (s/nilable ::s.accounts/item))
(def item ::item)
(s/def ::items (s/coll-of ::s.accounts/item))
(def items ::items)

;; Index

(s/def ::do-fetch-index-state keyword?)

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event vector?)
(s/def ::do-fetch-index-response (s/keys))

(s/def ::do-fetch-index-failed-cofx (s/keys))
(s/def ::do-fetch-index-failed-event vector?)
(s/def ::do-fetch-index-failed-response (s/keys))

;; Submit

(s/def :do-submit-account-response-http-xhrio/params :create-account-valid/params)
(s/def :do-submit-account-response/http-xhrio (s/keys :req-un [:do-submit-account-response-http-xhrio/params]))

(s/def ::do-submit-response-cofx (s/keys :req-un [:do-submit-account-response/http-xhrio]))
(def do-submit-response-cofx ::do-submit-response-cofx)

(s/def ::do-submit-response-event vector?)
(def do-submit-response-event ::do-submit-response-event)

(s/def ::do-submit-response (s/keys :req-un [:do-submit-account-response/http-xhrio]))
(def do-submit-response ::do-submit-response)
