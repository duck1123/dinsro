(ns dinsro.specs.events.forms.create-category
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::user-id string?)
(s/def ::shown? boolean?)
