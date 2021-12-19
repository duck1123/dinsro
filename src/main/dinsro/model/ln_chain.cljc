(ns dinsro.model.ln-chain
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [taoensso.timbre :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::chain string?)
(defattr chain ::chain :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::network string?)
(defattr network ::network :string
  {ao/identities #{::id}
   ao/schema     :production})

(def attributes [id chain network])