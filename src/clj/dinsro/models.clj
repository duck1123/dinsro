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

(s/defschema AuthenticationData
  {:email  s/Str
   :password s/Str})

(s/defschema RegistrationData
  {:name  s/Str
   :email s/Str
   :password s/Str})

(s/defschema Account
  {:id       s/Int
   :name     s/Str
   :email    s/Str
   :owner-id s/Int
   :created  DateTime
   :updated  DateTime})
