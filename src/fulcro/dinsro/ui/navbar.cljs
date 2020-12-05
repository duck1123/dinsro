(ns dinsro.ui.navbar
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def default-menu-links
  [{:navlink/name (tr [:accounts])
    :navlink/href :index-accounts-page}
   {:navlink/name (tr [:transactions])
    :navlink/href :index-transactions-page}])

(s/def ::expanded? boolean?)

(defsc NavLink
  [_this {:navlink/keys [name href]}]
  {:query [:navlink/id :navlink/name :navlink/href]
   :ident :navlink/id
   :initial-state (fn [{:navlink/keys [id name href]}]
                    {:navlink/id id
                     :navlink/name name
                     :navlink/href href})}
  (dom/a
   :.navbar-item
   {:href href
    :onClick (fn [evt] (.preventDefault evt) false)}
   name))

(def ui-nav-link (comp/factory NavLink {:keyfn :navlink/id}))

(defsc NavbarBurger
  [_this {:navbar/keys [expanded?]}]
  {:query [:navbar/expanded?]
   :initial-state {:navbar/expanded? true}}
  (dom/div
   :.navbar-burger.burger
   {:role :button
    :aria-label :menu
    :aria-expanded false
    :onClick (fn [] (timbre/info "clicked"))
    :className (when expanded? "is-active")}
   (dom/span {:aria-hidden true})
   (dom/span {:aria-hidden true})
   (dom/span {:aria-hidden true})))

(def ui-navbar-burger (comp/factory NavbarBurger))

(defsc NavbarBrand
  [_this {:navbar/keys [expanded?]}]
  (dom/div
   :.navbar-brand
   (dom/a
    :.navbar-item
    {:href "/"
     :style {:fontWeight :bold}}
    "Dinsro")
   (ui-navbar-burger {:navbar/expanded? expanded?})))

(def ui-navbar-brand (comp/factory NavbarBrand))

(defsc NavbarAuthLink
  [_this _props]
  (ui-nav-link {:navlink/id :user
                :navlink/name "User"
                :navlink/href "/users"}))

(def ui-navbar-auth-link (comp/factory NavbarAuthLink))

(defsc NavbarUnauthenticated
  [_this _props]
  (let [registration-enabled? true]
    (comp/fragment
     (ui-nav-link {:navlink/id :login
                   :navlink/name (tr [:login])
                   :navlink/href :login-page})
     (when registration-enabled?
       (ui-nav-link {:navlink/id :register
                     :navlink/name (tr [:register])
                     :navlink/href :register-page})))))

(def ui-navbar-unauthenticated (comp/factory NavbarUnauthenticated))

(defsc Navbar
  [_this {auth-id :auth/id
          :navbar/keys [auth-data
                        expanded?
                        menu-links
                        dropdown-menu-links]}]
  {:query [:auth/id :navbar/expanded?
           :navbar/auth-data
           :navbar/menu-links
           :navbar/dropdown-menu-links]
   :css [[:.navbar {:background-color "red"}]]
   :initial-state {:auth/id 1
                   :navbar/auth-data {}
                   :navbar/dropdown-menu-links []
                   :navbar/expanded? false
                   :navbar/menu-links []}}
  (dom/nav
   :.navbar.is-info
   (dom/div
    :.container
    {:aria-label "main navigation"
     :role "navigation"}
    (ui-navbar-brand {})
    (dom/div
     :.navbar-menu {:className (when expanded? "is-active")}
     (dom/div
      :.navbar-start
      (when auth-id
        (map ui-nav-link menu-links)))
     (dom/div
      :.navbar-end
      (if auth-id
        (comp/fragment
         (dom/div
          :.navbar-item.has-dropdown.is-hoverable
          (ui-navbar-auth-link auth-data)
          (dom/div
           :.navbar-dropdown
           (map ui-nav-link dropdown-menu-links))))
        (ui-navbar-unauthenticated {})))))))

(def ui-navbar (comp/factory Navbar))
