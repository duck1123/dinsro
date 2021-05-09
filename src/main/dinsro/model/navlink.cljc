(ns dinsro.model.navlink
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   ;; [com.wsscode.pathom.connect :as pc]
   ;; #?(:clj [dinsro.database-queries :as queries])
   [taoensso.timbre :as log])
  )

(defattr id :navlink/id :string
  {ao/identity? true
   ao/schema    :production})

(defattr name :navlink/name :string
  {ao/identities #{:navlink/id}
   ao/schema    :production})

(defattr href :navlink/href :string
  {ao/identities #{:navlink/id}
   ao/schema    :production})

(defattr path :navlink/path :string
  {ao/identities #{:navlink/id}
   ao/schema    :production})

(def attributes [id name href path])

#?(:clj
   (def resolvers []))
