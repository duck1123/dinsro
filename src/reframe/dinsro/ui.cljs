(ns dinsro.ui
  (:require
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def strings {})

(defn l
  [keyword]
  (get strings keyword (str "Missing string: " keyword)))
