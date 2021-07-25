(ns dinsro.ui.navbar
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.mutations :as mutations]
   [dinsro.mutations.session :as mu.session]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(s/def ::expanded? boolean?)

(defsc NavLink
  [this {:navlink/keys [href id name]}]
  {:ident         :navlink/id
   :initial-state {:navlink/id   ""
                   :navlink/href ""
                   :navlink/name ""}
   :query         [:navlink/id :navlink/name :navlink/href]}
  (dom/a :.navbar-item
    {:href    href
     :onClick (fn [evt]
                (.preventDefault evt)
                (.stopPropagation evt)
                (comp/transact! this [`(mutations/activate-nav-link ~{:navlink/id id})])
                false)}
    name))

(def ui-nav-link (comp/factory NavLink {:keyfn :navlink/id}))

(defsc NavbarAuthLink
  [_this {:navlink/keys [name href]}]
  {:ident         :navlink/id
   :initial-state {:navlink/id   ""
                   :navlink/href ""
                   :navlink/name ""}
   :query         [:navlink/id :navlink/name :navlink/href]}
  (dom/a :.navbar-link
    {:href    href
     :onClick (fn [evt] (.preventDefault evt) false)}
    name))

(def ui-navbar-auth-link (comp/factory NavbarAuthLink))

(defsc NavbarLogoutLink
  [this {:navlink/keys [href]}]
  {:ident         (fn [_] [:navlink/id :logout])
   :initial-state {:navlink/id   ""
                   :navlink/href "/logout"}
   :query         [:navlink/id :navlink/name :navlink/href]}
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
  [this {::keys        [auth-links dropdown-links expanded? menu-links unauth-links]
         :session/keys [current-user]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :menu-links NavLink
               {:target [:component/id ::Navbar ::menu-links]})

     (df/load! this :dropdown-links NavLink
               {:target [:component/id ::Navbar ::dropdown-links]})

     (df/load! this [:navlink/id "transactions"] NavbarAuthLink
               {:target [:component/id ::Navbar ::auth-links]})

     (df/load! this :unauth-links NavLink
               {:target [:component/id ::Navbar ::unauth-links]}))
   :css           [[:.navbar {:background-color "red"}]]
   :ident         (fn [_] [:component/id ::Navbar])
   :initial-state {::auth-links          {}
                   ::dropdown-links      []
                   ::expanded?           false
                   ::menu-links          []
                   ::unauth-links        []
                   :session/current-user {:user/valid? false}}
   :query         [{::auth-links (comp/get-query NavbarAuthLink)}
                   [:session/current-user '_]
                   {::dropdown-links (comp/get-query NavLink)}
                   ::expanded?
                   {::menu-links (comp/get-query NavLink)}
                   {::unauth-links (comp/get-query NavLink)}]}
  (let [valid? (boolean (:user/valid? current-user))]
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
                 (ui-navbar-auth-link auth-links)
                 (dom/div :.navbar-dropdown
                   (map ui-nav-link dropdown-links)
                   (ui-navbar-logout-link {}))))
              (map ui-nav-link unauth-links))))))))

(def ui-navbar (comp/factory Navbar))
