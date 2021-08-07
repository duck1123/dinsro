(ns dinsro.ui.navbar
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.users :as m.users]
   [dinsro.mutations :as mutations]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(s/def ::expanded? boolean?)

(defsc NavLink
  [this {::m.navlink/keys [href id name]}]
  {:ident         ::m.navlink/id
   :initial-state {::m.navlink/id   nil
                   ::m.navlink/href ""
                   ::m.navlink/name ""}
   :query         [::m.navlink/id ::m.navlink/name ::m.navlink/href]}
  (dom/a :.navbar-item
    {:href    href
     :onClick (fn [evt]
                (.preventDefault evt)
                (.stopPropagation evt)
                (comp/transact! this [`(mutations/activate-nav-link ~{::m.navlink/id id})])
                false)}
    name))

(def ui-nav-link (comp/factory NavLink {:keyfn ::m.navlink/id}))

(defsc NavbarAuthLink
  [_this {::m.users/keys [id name]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   nil
                   ::m.users/name ""}
   :query         [::m.users/id ::m.users/name]}
  (let [href (str "/users/" (str id))]
    (dom/a :.navbar-link {:href href} name)))

(def ui-navbar-auth-link (comp/factory NavbarAuthLink))

(defsc NavbarLogoutLink
  [this {::m.navlink/keys [href]}]
  {:ident         (fn [_] [::m.navlink/id "logout"])
   :initial-state {::m.navlink/id   "logout"
                   ::m.navlink/href "/logout"}
   :query         [::m.navlink/id ::m.navlink/name ::m.navlink/href]}
  (dom/a :.navbar-item
    {:href    href
     :onClick (fn [evt]
                (.preventDefault evt)
                (comp/transact! this [(mu.session/logout {})])
                false)}
    "Logout"))

(def ui-navbar-logout-link (comp/factory NavbarLogoutLink))

(defn toggle
  []
  `(toggle))

(defn navbar-burger
  [expanded? burger-clicked]
  (dom/div :.navbar-burger.burger
    {:role          :button
     :aria-label    :menu
     :aria-expanded false
     :onClick       burger-clicked
     :className     (when expanded? "is-active")}
    (dom/span {:aria-hidden true})
    (dom/span {:aria-hidden true})
    (dom/span {:aria-hidden true})))

(defn navbar-brand
  [expanded? burger-clicked]
  (dom/div :.navbar-brand
    (dom/a :.navbar-item
      {:href  "/"
       :style {:fontWeight :bold}}
      "Dinsro")
    (navbar-burger expanded? burger-clicked)))

(defsc Navbar
  [this {::keys           [expanded?]
         ::m.navlink/keys [dropdown-links menu-links unauth-links]
         :session/keys    [current-user-ref]
         :as              props}]
  {:css           [[:.navbar {:background-color "red"}]]
   :ident         (fn [_] [:component/id ::Navbar])
   :initial-state {::m.navlink/dropdown-links []
                   ::expanded?                false
                   ::m.navlink/menu-links     []
                   ::m.navlink/unauth-links   []
                   :session/current-user-ref  {}}
   :query         [[::auth/authorization :local]
                   {:session/current-user-ref (comp/get-query NavbarAuthLink)}
                   {::m.navlink/dropdown-links (comp/get-query NavLink)}
                   ::expanded?
                   {::m.navlink/menu-links (comp/get-query NavLink)}
                   {::m.navlink/unauth-links (comp/get-query NavLink)}]}
  (let [valid? (some-> props
                       (get [::auth/authorization :local])
                       :session/current-user-ref
                       second)]
    (dom/nav :.navbar.is-info
      (dom/div :.container
        {:aria-label "main navigation"
         :role       "navigation"}
        (navbar-brand expanded? #(comp/transact! this [`(dinsro.mutations/toggle)]))
        (dom/div :.navbar-menu
          {:className (when expanded? "is-active")}
          (dom/div :.navbar-start (when valid? (map ui-nav-link menu-links)))
          (dom/div :.navbar-end
            (if valid?
              (comp/fragment
               (dom/div :.navbar-item.has-dropdown.is-hoverable
                 (ui-navbar-auth-link current-user-ref)
                 (dom/div :.navbar-dropdown
                   (map ui-nav-link dropdown-links)
                   (ui-navbar-logout-link {}))))
              (map ui-nav-link unauth-links))))))))

(def ui-navbar (comp/factory Navbar))
