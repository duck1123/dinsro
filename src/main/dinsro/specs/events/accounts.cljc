(ns dinsro.specs.events.accounts
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.accounts :as s.a.accounts]
   [taoensso.timbre :as timbre]))

(s/def ::item (s/nilable ::m.accounts/item))
(def item ::item)
(s/def ::items (s/coll-of ::m.accounts/item))
(def items ::items)

(s/def ::sub-item-event
  (s/cat
   :event-name keyword?
   :id         :db/id))

(comment
  (ds/gen-key ::sub-item-event))

;; Index


(s/def ::do-fetch-index-state ::ds/state)

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event vector?)
(s/def ::do-fetch-index-response (s/keys))

(s/def ::do-fetch-index-failed-cofx (s/keys))
(s/def ::do-fetch-index-failed-event vector?)
(s/def ::do-fetch-index-failed-response (s/keys))

;; Submit

(s/def :do-submit-account-response-http-xhrio/params ::s.a.accounts/create-params-valid)
(s/def :do-submit-account-response/http-xhrio (s/keys :req-un [:do-submit-account-response-http-xhrio/params]))

(s/def ::do-submit-response-cofx (s/keys :req-un [:do-submit-account-response/http-xhrio]))
(def do-submit-response-cofx ::do-submit-response-cofx)

(s/def ::do-submit-response-event vector?)
(def do-submit-response-event ::do-submit-response-event)

(s/def ::do-submit-response (s/keys :req-un [:do-submit-account-response/http-xhrio]))
(def do-submit-response ::do-submit-response)
