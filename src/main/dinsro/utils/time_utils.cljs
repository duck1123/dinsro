(ns dinsro.utils.time-utils)

(defn get-date-string
  [date]
  (str (.getFullYear date) "-" (inc (.getMonth date)) "-0" (.getDate date)))

(defn get-time-string
  [date]
  (str (.getHours date) ":" (.getMinutes date)))
