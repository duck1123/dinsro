(ns dinsro.model.currencies
  (:require [dinsro.db.core :as db]
            [taoensso.timbre :as timbre]))

(defn index
  []
  #_(db/list-currencies)
  nil)

(defn create-record
  [params]
  #_(db/create-currency! (assoc params :id nil))
  nil)
