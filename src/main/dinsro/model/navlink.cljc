(ns dinsro.model.navlink
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.wsscode.pathom.connect :as pc]
   ;; #?(:clj [dinsro.database-queries :as queries])
   [taoensso.timbre :as log]))

(defattr id :navlink/id :string
  {ao/identity? true
   ao/schema    :production})

(defattr name :navlink/name :string
  {ao/identities #{:navlink/id}
   ;; ::pc/resolve (fn [] {:navlink/name "fred"})
   ao/schema    :production
   })

(defattr href :navlink/href :string
  {ao/identities #{:navlink/id}
   ao/schema    :production})

(defattr all-navlinks :navlink/all-navlinks :ref
  {ao/target :navlink/id
   ::pc/output [{:navlink/all-navlinks [:navlink/id]}]
   ::pc/resolve (fn [_env _]
                  {:navlink/all-navlinks []})})

(def attributes [id name href all-navlinks])

#?(:clj
   (def resolvers []))
