(ns dinsro.specs.settings
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::allow-registration boolean?)
(s/def ::first-run boolean?)

(s/def ::settings
  (s/keys :req-un
          [::allow-registration
           ::first-run]))
(def settings ::settings)
