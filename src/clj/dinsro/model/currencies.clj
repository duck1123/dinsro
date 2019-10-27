(ns dinsro.model.currencies
  (:require [dinsro.db.core :as db]
            [taoensso.timbre :as timbre]))

(defn index
  []
  (db/list-currencies)
  #_[{:id 1 :name "sats" :is-primary true}
   {:id 2 :name "dollars" :exchange 0.7}])

(defn create-record
  [params]
  (db/create-currency! (assoc params :id nil)))
