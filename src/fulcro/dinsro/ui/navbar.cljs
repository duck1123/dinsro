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
  {:ident :navlink/id
   :initial-state {:navlink/id   0
                   :navlink/href ""
                   :navlink/name ""}
   :query [:navlink/id :navlink/name :navlink/href]}
  (dom/a
   :.navbar-item
   {:href href
    :onClick (fn [evt] (.preventDefault evt) false)}
   name))

(def ui-nav-link (comp/factory NavLink {:keyfn :navlink/id}))

(defsc NavbarAuthLink
  [_this {:navlink/keys [name href]}]
  {:ident :navlink/id
   :initial-state {:navlink/id   0
                   :navlink/href ""
                   :navlink/name ""}
   :query [:navlink/id :navlink/name :navlink/href]}
  (dom/a
   :.navbar-link
   {:href href
    :onClick (fn [evt] (.preventDefault evt) false)}
   name))

(def ui-navbar-auth-link (comp/factory NavbarAuthLink))

(defsc NavbarBurger
  [_this {:navbar/keys [expanded?]}]
  {:initial-state {:navbar/expanded? true}
   :query [:navbar/expanded?]}
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
  {:initial-state {:navbar/expanded? true}
   :query [:navbar/expanded?]}
  (dom/div
   :.navbar-brand
   (dom/a
    :.navbar-item
    {:href "/"
     :style {:fontWeight :bold}}
    "Dinsro")
   (ui-navbar-burger {:navbar/expanded? expanded?})))

(def ui-navbar-brand (comp/factory NavbarBrand))

(defsc NavbarUnauthenticated
  [_this _props]
  (let [registration-enabled? true]
    (comp/fragment
     (ui-nav-link {:navlink/id :login
                   :navlink/name (tr [:login])
                   :navlink/href :login})
     (when registration-enabled?
       (ui-nav-link {:navlink/id :register
                     :navlink/name (tr [:register])
                     :navlink/href :register})))))

(def ui-navbar-unauthenticated (comp/factory NavbarUnauthenticated))

;; :session/current-user {:user/valid? true}

(defsc Navbar
  [_this {::keys [auth-links dropdown-links expanded? menu-links navbar-brand]}]
  {:css [[:.navbar {:background-color "red"}]]
   :ident (fn [_] [:component/id ::Navbar])
   :initial-state {::auth-links     {}
                   ::dropdown-links []
                   ::expanded?      false
                   ::menu-links     []
                   ::navbar-brand   {}}
   :query [{::auth-links     (comp/get-query NavbarAuthLink)}
           :auth/id
           {::dropdown-links (comp/get-query NavLink)}
           ::expanded?
           {::menu-links     (comp/get-query NavLink)}
           {::navbar-brand   (comp/get-query NavbarBrand)}]}
  (let [valid? true]
    (dom/nav
     :.navbar.is-info
     (dom/div
      :.container
      {:aria-label "main navigation"
       :role "navigation"}
      (ui-navbar-brand navbar-brand)
      (dom/div
       :.navbar-menu {:className (when expanded? "is-active")}
       (dom/div
        :.navbar-start
        (when valid?
          (map ui-nav-link menu-links)))
       (dom/div
        :.navbar-end
        (if valid?
          (comp/fragment
           (dom/div
            :.navbar-item.has-dropdown.is-hoverable
            ;; (ui-nav-link [:navlink/id :users])
            (ui-navbar-auth-link auth-links)
            (dom/div
             :.navbar-dropdown
             (map ui-nav-link dropdown-links))))
          (ui-navbar-unauthenticated {}))))))))

(def ui-navbar (comp/factory Navbar))
