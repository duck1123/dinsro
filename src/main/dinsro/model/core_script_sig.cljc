(ns dinsro.model.core-script-sig
  (:refer-clojure :exclude [key])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [taoensso.timbre :as log]))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::key string?)
(defattr key ::key :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::value string?)
(defattr value ::value :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::tx-in uuid?)
(defattr tx-in ::tx-in :ref
  {ao/identities #{::id}
   ao/target     ::m.core-tx-in/id
   ao/schema     :production})

(def attributes [id key value])
