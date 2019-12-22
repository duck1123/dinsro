(ns dinsro.utils)

(defn try-parse
  [v]
  (try (Integer/parseInt v) (catch NumberFormatException e nil)))

(defn get-as-int
  [params key]
  (try
    (some-> params key str Integer/parseInt)
    (catch NumberFormatException e nil)))
