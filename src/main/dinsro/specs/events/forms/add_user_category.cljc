(ns dinsro.specs.events.forms.add-user-category
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::name string?)
(def name ::name)

(s/def ::user-id string?)
(def user-id ::user-id)
