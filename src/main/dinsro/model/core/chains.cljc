(ns dinsro.model.core.chains
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::chain string?)
(defattr chain ::chain :string
  {ao/identities #{::id}
   ao/schema     :production})

(def attributes [id chain])
