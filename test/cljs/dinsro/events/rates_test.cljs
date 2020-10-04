(ns dinsro.events.rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.events.rates :as e.rates]
   [dinsro.spec :as ds]
   [dinsro.spec.events.rates :as s.e.rates]
   [dinsro.spec.rates :as s.rates]
   [expound.alpha :as expound]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.events.rates-test
 "Rate Events" [])

(let [rates (ds/gen-key (s/coll-of ::e.rates/item :count 3))
      item-map (into {} (map
                         (fn [rate] [(:db/id rate) rate])
                         rates))
      item-sub-cofx {::e.rates/item-map item-map}]

  (comment (defcard rates rates))
  (comment (defcard item-map item-map))
  (comment (defcard item-sub-cofx item-sub-cofx))

  (deftest sub-item-no-match
    (let [unused-id (inc (last (sort (map :db/id rates ))))]
        (is (= nil (e.rates/item-sub item-sub-cofx [::e.rates/item unused-id])))))

  (let [item (second (first item-map))
        id (ffirst item-map)
        event [::e.rates/item id]
        response (e.rates/item-sub item-sub-cofx event)]

    (comment (defcard id (pr-str id)))
    (comment (defcard item item))

    (deftest sub-item-match
      (is (= item response))
      (s/assert ::s.rates/item response)
      (expound/expound-str ::s.rates/item response))))

(defcard "**Fetch Record**")

(defcard "* **do-fetch-record-failed**")

(let [cofx {}
      event (ds/gen-key ::e.rates/do-fetch-record-failed-event)]
  (defcard do-fetch-record-failed-cofx cofx)
  (defcard do-fetch-record-failed-event event)
  (comment
    (deftest do-fetch-record-failed
      (is (= nil (e.rates/do-fetch-record cofx event))))))

(defcard "* **do-fetch-record**")

(let [cofx {}
      event (ds/gen-key ::e.rates/do-fetch-record-event)]
  (defcard cofx cofx)
  (defcard do-fetch-record-event event)
  (comment
    (deftest do-fetch-record
      (is (= nil (e.rates/do-fetch-record cofx event))))))


(defcard "**Add record**")

(let [cofx {}]
  (defcard add-record-cofx cofx)
  (let [event (ds/gen-key ::s.e.rates/add-record-event-without-response)]
    (defcard add-record-event-without-response event)
    (deftest add-record-no-response
      (let [id (first event)]
        (is (= {:dispatch [::e.rates/do-fetch-record id [::e.rates/add-record id]]}
               (e.rates/add-record cofx event))))))
  (let [event (ds/gen-key ::s.e.rates/add-record-event-with-response)]
    (defcard add-record-event-with-response event)
    (deftest add-record-response
      (is (= {} (e.rates/add-record cofx event))))))
