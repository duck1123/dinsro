(ns dinsro.events.transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.event_utils :as eu]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.events.transactions :as s.e.transactions]
   [dinsro.spec :as ds]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.transactions-test
 "Transaction Events"
 [#{:events} #{:transactions} #{:transactions :events}])

;; (defcard ::s.transactions/item
;;   (ds/gen-key ::s.transactions/item))

(defcard ::s.transactions/item
  (ds/gen-key ::e.transactions/item))

;; (defcard ::s.e.transactions/do-fetch-index-cofx
;;   (ds/gen-key ::s.e.transactions/do-fetch-index-cofx))

(defcard ::e.transactions/do-fetch-index-cofx
  (ds/gen-key ::s.e.transactions/do-fetch-index-cofx))

(defcard do-fetch-index-response
  "7"
  )

;; (defcard ::s.e.transactions/do-fetch-index-event
;;   (ds/gen-key ::s.e.transactions/do-fetch-index-event))

;; (defcard ::s.e.transactions/do-fetch-index-response
;;   (ds/gen-key ::s.e.transactions/do-fetch-index-response))

(defcard do-fetch-index-response-card
  (ds/gen-key ::e.transactions/do-fetch-index-response))

(deftest do-fetch-index
  (let [cofx {}
        event [{:foo "bar"}]]
    (is (= 1 (eu/do-fetch-index
              'dinsro.events.transactions
              cofx event)))))

;; (defcard ::s.e.transactions/do-submit-cofx
;;   (ds/gen-key ::s.e.transactions/do-submit-cofx))

;; (defcard ::s.e.transactions/do-submit-event
;;   (ds/gen-key ::s.e.transactions/do-submit-event))

;; (defcard ::s.e.transactions/do-submit-response
;;   (ds/gen-key ::s.e.transactions/do-submit-response))
