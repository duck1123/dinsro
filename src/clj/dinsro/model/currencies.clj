(ns dinsro.model.currencies
  (:require [dinsro.db.core :as db]
            [taoensso.timbre :as timbre]))

(defn index
  []
  [{:id 1 :name "sats" :is-primary true}
   {:id 2 :name "dollars" :exchange 0.7}])
