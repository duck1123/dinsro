(ns dinsro.models
  (:require [schema.core :as s]))

(s/defschema User
  {:id   Long
   :name s/Str})
