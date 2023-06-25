(ns dinsro.joins.navbars
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.specs]))

;; [[../model/navbars.cljc]]
;; [[../ui/navbars.cljs]]

(defattr parent-navbar ::parent-navbar :ref
  {ao/cardinality :one
   ao/pc-input    #{::m.navbars/id}
   ao/pc-output   [{::parent-navbar [::m.navbars/id]}]
   ao/pc-resolve  (fn [_env props]
                    (let [navbar-id (::m.navbars/id props)
                          navbar    (get m.navbars/menus navbar-id)
                          parent-id (::m.navbars/parent navbar)]
                      {::parent-navbar (when parent-id (m.navbars/ident parent-id))}))
   ao/target      ::m.navbars/id})

(defattr index ::index :ref
  {ao/pc-output  [{::index [::m.navbars/id]}]
   ao/pc-resolve (fn [_env _props] {::index (m.navbars/idents (keys m.navbars/menus))})
   ao/target     ::m.navbars/id})

(def attributes [index parent-navbar])
