(ns dinsro.formatters.date-time
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.specs :as ds]
   [taoensso.timbre :as log]
   [tick.alpha.api :as t]
   [tick.locale-en-us])
  (:import
   #?(:clj (java.time LocalDateTime ZoneId Instant))
   #?(:clj (java.time.format DateTimeFormatter))
   #?(:clj (java.util Date))
   #?(:cljs goog.i18n.DateTimeFormat)))

#?(:clj (def ^:private ^:static ^ZoneId UTC (ZoneId/of "UTC")))

(defn ->iso
  [inst]
  (let [dt  (-> inst (t/in ds/default-timezone) (t/date-time))
        dts (str dt)]
    (log/info :->iso/dt {:dt dt :dts dts})
    dts))

(defn date-year
  [date]
  #?(:cljs
     (assert (instance? js/Date date) (str "`date` " date ": " (type date) " isn't js/Date")))
  #?(:clj  (.getYear ^LocalDateTime date)
     :cljs (.getFullYear date)))

(defn current-year []
  (date-year
   #?(:clj  (LocalDateTime/now UTC)
      :cljs (js/Date.))))

(def fmt #?(:clj (-> (DateTimeFormatter/ofPattern "MMM d")
                     (.withZone UTC))
            :cljs (DateTimeFormat. "MMM d")))

#?(:clj (defn ->local-date ^LocalDateTime [^Date date]
          (condp instance? date
            Date          (->local-date (.toInstant date))
            Instant       (LocalDateTime/ofInstant date UTC)
            LocalDateTime date))
   :cljs (defn ->local-date
           [date]
           (.log js/console "date" #js {"date" (.toISOString date)})
           date))

(defn format-date [^Instant instant]
  (if instant
    (let [date #?(:clj (->local-date instant)
                  :cljs instant)
          year (date-year date)]
      (cond-> (.format fmt date)
        (not= year (current-year)) (str ", " year)))
    "N/A"))

(defn date-formatter
  "RAD report formatter that can be set on a RAD attribute (`::report/field-formatter`)"
  [_report-instance value]
  (format-date value))

(comment
  (format-date #inst "2019-08-31T22:00:00.000-00:00") ; => "Aug 31, 2019"

  (->local-date #inst "2019-08-31T22:00:00.000-00:00")

  nil)
