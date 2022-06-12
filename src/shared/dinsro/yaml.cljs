(ns dinsro.yaml
  (:require ["js-yaml" :as yaml]))

(defn generate-string
  [o]
  (yaml/dump (clj->js o)))
