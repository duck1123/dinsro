(ns dinsro.model.navbars
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.model.navlinks :as m.navlinks]
   [lambdaisland.glogc :as log]))

;; [[../joins/navbars.cljc]]
;; [[../ui/navbars.cljs]]

#?(:cljs (comment ::m.navlinks/id))

(defonce menus-atom (atom {}))

(defn defmenu
  [key options]
  (swap! menus-atom assoc key options))

(defmenu
;; Main top bar
  :main
  {::parent   :root
   ::children [:accounts
               :transactions
               :contacts
               :nostr-events
               :settings
               :admin]})

(defmenu :sidebar
  {::parent   :root
   ::children [:home
               :accounts
               :transactions
               :contacts
               :nostr-events
               :settings
               :admin]})

(defmenu :unauth
  {::parent   :root
   ::children [:login
               :registration]})

(defattr id ::id :uuid
  {ao/identity? true})

(defattr children ::children :ref
  {ao/identities #{::id}
   ao/target     ::m.navlinks/id
   ao/pc-input   #{::id}
   ao/pc-output  [{::children [::m.navlinks/id]}]
   ao/pc-resolve (fn [_env {::keys [id]}]
                   (if-let [navbar (get @menus-atom id)]
                     {::children (m.navlinks/idents (::children navbar []))}
                     {}))})

(defattr child-count ::child-count :number
  {ao/identities #{::id}
   ao/pc-input   #{::children}
   ao/pc-output  [::child-count]
   ao/pc-resolve (fn [_ {::keys [children]}] {::child-count (count children)})})

(>def ::ident (s/keys :req [::id]))
(>defn ident [id] [::id => ::ident] {::id id})
(>defn idents [ids] [(s/coll-of ::id) => (s/coll-of ::ident)] (mapv ident ids))

(s/def ::id keyword?)
(defattr parent ::parent :ref
  {ao/identities #{::id}
   ao/target     ::id
   ao/pc-input   #{::id}
   ao/pc-output  [{::parent [::id]}]
   ao/pc-resolve
   (fn [_env props]
     (log/info :parent/starting {:props props})
     (let [{::keys [id]} props]
       (if-let [navbar (get @menus-atom id)]
         (let [{::keys [parent]} navbar]
           {::parent (when parent (ident parent))})
         {})))})

(defattr authenticated ::authenticated :ref
  {ao/target     ::id
   ao/pc-output  [{::authenticated [::id]}]
   ao/pc-resolve (fn [_env _props] {::authenticated {::id :main}})})

(defattr sidebar ::sidebar :ref
  {ao/target     ::id
   ao/pc-output  [{::sidebar [::id]}]
   ao/pc-resolve (fn [_env _props] {::sidebar {::id :sidebar}})})

(defattr unauthenticated ::unauthenticated :ref
  {ao/target     ::id
   ao/pc-output  [{::unauthenticated [::id]}]
   ao/pc-resolve (fn [_env _props] {::unauthenticated {::id :unauth}})})

(def attributes [id children parent authenticated sidebar unauthenticated
                 child-count])
