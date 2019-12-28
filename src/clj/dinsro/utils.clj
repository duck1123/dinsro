(ns dinsro.utils
  (:require [taoensso.timbre :as timbre]))

(defn parse-int
  [v]
  (Integer/parseInt v))

(defn parse-double
  [v]
  (Double/parseDouble v))

(defn try-parse-int
  [v]
  (try
    (parse-int v)
    (catch NumberFormatException e
      (timbre/error e)
      nil)))

(defn try-parse-double
  [v]
  (try
    (parse-double v)
    (catch NumberFormatException e
      (timbre/error e)
      nil)))

(defn get-as-int
  [params key]
  (try
    (some-> params key str parse-int)
    (catch NumberFormatException e nil)))
