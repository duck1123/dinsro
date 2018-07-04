(ns dinsro.models
  (:require [schema.core :as s])
  (:import org.joda.time.DateTime))

(s/defschema User
  {(s/optional-key :id)      s/Int
   :name                     s/Str
   :email                    s/Str
   :password-hash            s/Str
   (s/optional-key :created) DateTime
   (s/optional-key :updated) DateTime})

(s/defschema RegistrationData
  {:name  s/Str
   :email s/Str})
