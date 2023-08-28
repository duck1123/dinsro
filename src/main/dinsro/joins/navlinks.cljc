(ns dinsro.joins.navlinks
  (:require
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.specs]
   [lambdaisland.glogc :as log]))

;; [[../model/navlinks.cljc]]
;; [[../ui/navlinks.cljs]]

(defn find-path
  ([router]
   (find-path router []))
  ([router path]
   (log/trace :find-path/starting {:path path :router router})
   (if router
     (do
       (log/trace :find-path/current {:path path :router router})
       (let [next-router (get-in @m.navlinks/routes-atom [router ::m.navlinks/router])]
         (log/trace :find-path/next {:next-router next-router})
         (recur next-router (vec (concat [router] path)))))
     ;; no more items
     path)))

;; the list of parent pages to the root
(defattr path ::path :ref
  {ao/identities #{::m.navlinks/id}
   ao/pc-input   #{::m.navlinks/id}
   ao/target     ::m.navlinks/id
   ao/pc-output  [{::path [::m.navlinks/id]}]
   ao/pc-resolve
   (fn [_env {::m.navlinks/keys [id]}]
     (log/trace :path/starting {:id id})
     (let [router (get-in @m.navlinks/routes-atom [id ::m.navlinks/router])
           path   (find-path router)]
       (log/trace :path/path-found {:path path})
       (let [homed-path (if (#{:home} id) [] path)
             idents     (m.navlinks/idents homed-path)]
         (log/debug :path/finished {:id id :idents idents})
         {::path idents})))})

(defattr menu ::menu :ref
  {ao/identities #{::m.navlinks/id}
   ao/pc-input   #{::m.navlinks/id}
   ao/target     ::m.navbars/id
   ao/pc-output  [{::menu [::m.navbars/id]}]
   ao/pc-resolve
   (fn [_env {::m.navlinks/keys [id]}]
     (if (get @m.navbars/menus-atom id)
       {::menu {::m.navbars/id id}}
       {::menu nil}))})

(defattr index ::index :ref
  {ao/pc-output  [{::index [::m.navlinks/id]}]
   ao/pc-resolve (fn [_env _props]
                   (let [ids    (sort (keys @m.navlinks/routes-atom))
                         idents (m.navlinks/idents ids)]
                     {::index idents}))
   ao/target     ::m.navlinks/id})

(def attributes [path index menu])

(comment

  ((:com.wsscode.pathom.connect/resolve path)
   {}
   {::m.navlinks/id :admin-ln-remote-nodes})

  ((:com.wsscode.pathom.connect/resolve path)
   {}
   {::m.navlinks/id :admin})

  (find-path :admin-ln-remote-nodes)
  (find-path :admin)

  nil)
