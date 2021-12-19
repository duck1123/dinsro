(ns dinsro.model.core-tx-out
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-tx :as m.core-tx]
   [taoensso.timbre :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::n number?)
(defattr n ::n :int
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value number?)
(defattr value ::value :int
  {ao/identities #{::id}
   ao/schema     :production})

(defattr transaction ::transaction :ref
  {ao/identities #{::id}
   ao/target     ::m.core-tx/id
   ao/schema     :production})

(s/def ::params
  (s/keys :req [::n ::value ::transaction]))
(s/def ::item
  (s/keys :req [::id ::n ::value ::transaction]))

(def attributes [id n value transaction])
