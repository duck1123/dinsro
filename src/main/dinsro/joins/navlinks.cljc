(ns dinsro.joins.navlinks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../model/navlinks.cljc]]
;; [[../ui/navlinks.cljs]]
;; [[../../../notebooks/dinsro/notebooks/navlinks_notebook.clj]]

(def model-key o.navlinks/id)

(defn find-path
  ([router]
   (find-path router []))
  ([router path]
   (log/trace :find-path/starting {:path path :router router})
   (if router
     (do
       (log/trace :find-path/current {:path path :router router})
       (let [next-router (get-in @m.navlinks/routes-atom [router o.navlinks/router])]
         (log/trace :find-path/next {:next-router next-router})
         (recur next-router (vec (concat [router] path)))))
     ;; no more items
     path)))

(defn find-path2
  ([navlink-id] (find-path2 navlink-id []))
  ([navlink-id path]
   (if-let [item (@m.navlinks/routes-atom navlink-id)]
     (if-let [parent-id (o.navlinks/parent-key item)]
       (find-path2 parent-id (concat path [navlink-id]))
       (conj path navlink-id))
     (throw (ex-info "Failed to find item" {:navlink-id navlink-id})))))

;; the list of parent pages to the root
(defattr path ::path :ref
  {ao/identities #{model-key}
   ao/pc-input   #{model-key}
   ao/target     model-key
   ao/pc-output  [{::path [model-key]}]
   ao/pc-resolve
   (fn [_env {id model-key}]
     (log/trace :path/starting {:id id})
     (let [router (get-in @m.navlinks/routes-atom [id o.navlinks/router])
           path   (find-path router)]
       (log/trace :path/path-found {:path path})
       (let [homed-path (if (#{:home} id) [] path)
             idents     (m.navlinks/idents homed-path)]
         (log/debug :path/finished {:id id :idents idents})
         {::path idents})))})

(defattr menu ::menu :ref
  {ao/identities #{model-key}
   ao/pc-input   #{model-key}
   ao/target     o.navbars/id
   ao/pc-output  [{::menu [o.navbars/id]}]
   ao/pc-resolve
   (fn [_env {id model-key}]
     (if (get @m.navbars/menus-atom id)
       {::menu {o.navbars/id id}}
       {::menu nil}))})

(defattr index ::index :ref
  {ao/pc-output  [{::index [model-key]}]
   ao/pc-resolve (fn [_env _props]
                   (let [ids    (sort (keys @m.navlinks/routes-atom))
                         idents (m.navlinks/idents ids)]
                     {::index idents}))
   ao/target     model-key})

(def attributes [path index menu])
