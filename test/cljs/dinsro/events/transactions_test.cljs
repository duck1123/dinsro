(ns dinsro.events.transactions-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard defcard-rg deftest]]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.transactions :as s.transactions]
            [expound.alpha :as expound]
            [taoensso.timbre :as timbre]))

(defcard
  "test"
  )
