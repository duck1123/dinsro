(ns dinsro.utils
  #?(:cljs (:refer-clojure :exclude [uuid]))
  (:require [com.fulcrologic.guardrails.core :refer [>defn =>]]
            [clojure.spec.alpha :as s]
            [taoensso.timbre :as timbre]))

;; TODO: Find the source of this code
(>defn uuid
  "Generate a UUID the same way via clj/cljs.  Without args gives random UUID. With args, builds UUID based on input (which
  is useful in tests)."
  #?(:clj
     ([] [=> uuid?]
         (java.util.UUID/randomUUID)))
  #?(:clj
     ([int-or-str] [(s/or :i int? :s string?) => uuid?]
                   (if (int? int-or-str)
                     (java.util.UUID/fromString
                      (format "ffffffff-ffff-ffff-ffff-%012d" int-or-str))
                     (java.util.UUID/fromString int-or-str))))
  #?(:cljs
     ([] [=> uuid?]
         (random-uuid)))
  #?(:cljs
     ([& args] [(s/* any?) => uuid?]
               (cljs.core/uuid (apply str args)))))

#?(:clj
   (defn parse-int
     [v]
     (Integer/parseInt v)))

#?(:clj
   (defn parse-double
     [v]
     (Double/parseDouble v)))

#?(:clj
   (defn try-parse-int
     [v]
     (try
       (parse-int v)
       (catch NumberFormatException e
         (timbre/error e)
         nil))))

#?(:clj
   (defn try-parse-double
     [v]
     (try
       (parse-double v)
       (catch NumberFormatException e
         (timbre/error e)
         nil))))

#?(:clj
   (defn get-as-int
     [params k]
     (try
       (some-> params k str parse-int)
       (catch NumberFormatException _ nil))))
