(ns dinsro.model
  (:require
   [com.fulcrologic.rad.attributes :as attr]))

(def all-attributes
  (vec (concat)))

(def all-attribute-validator (attr/make-attribute-validator all-attributes))
