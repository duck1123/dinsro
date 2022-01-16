(ns dinsro.ui.navbar
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.home :as u.home]
   [taoensso.timbre :as log]))

(s/def ::expanded? boolean?)

(defsc NavLink
  [this {::m.navlink/keys [name target auth-link?]}]
  {:ident         ::m.navlink/id
   :initial-state {::m.navlink/id         nil
                   ::m.navlink/name       ""
                   ::m.navlink/auth-link? false
                   ::m.navlink/target     nil}
   :query         [::m.navlink/auth-link?
                   ::m.navlink/id
                   ::m.navlink/name
                   ::m.navlink/target]}
  (dom/a :.ui.item
    {:onClick (fn [e]
                (.preventDefault e)
                (if-let [component (comp/registry-key->class target)]
                  (do
                    (uism/trigger! this ::navbarsm :event/hide {})
                    (uism/trigger! this auth/machine-id :event/cancel {})
                    (if auth-link?
                      (auth/authenticate! this :local nil)
                      (rroute/route-to! this component {})))
                  (log/infof "Could not find target: %s" target)))}
    name))

(def ui-nav-link (comp/factory NavLink {:keyfn ::m.navlink/id}))

(defsc NavbarAuthLink
  [this {::m.users/keys [name id]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   nil
                   ::m.users/name ""}
   :query         [::m.users/id
                   ::m.users/name]}
  (let [component (comp/registry-key->class :dinsro.ui.users/UserForm)]
    (dom/a :.navbar-link
      {:onClick (fn [_e] (form/view! this component id))}
      name)))

(def ui-navbar-auth-link (comp/factory NavbarAuthLink))

(defsc NavbarLoginLink
  [this _]
  {:initial-state {}
   :query         []}
  (dom/a {:classes [:.ui.item]
          :onClick (fn [evt]
                     (.preventDefault evt)
                     (auth/authenticate! this :local nil)
                     (uism/trigger! this ::navbarsm :event/hide {})
                     false)}
    "Login"))

(def ui-navbar-login-link (comp/factory NavbarLoginLink))

(defsc NavbarLogoutLink
  [this _]
  {:initial-state {}
   :query         []}
  (dom/a :.ui.item
    {:onClick (fn [_evt]
                (uism/trigger! this ::navbarsm :event/hide {})
                (auth/logout! this :local)
                (let [component (comp/registry-key->class :dinsro.ui.home/HomePage)]
                  (rroute/route-to! this component {})))}
    "Logout"))

(def ui-navbar-logout-link (comp/factory NavbarLogoutLink))

(defsc NavbarSidebar
  [this {::m.navbar/keys [dropdown-links]
         :as             props}]
  {:ident         ::m.navbar/id
   :query         [::m.navbar/id
                   {::m.navbar/dropdown-links (comp/get-query NavLink)}
                   :inverted
                   [::auth/authorization :local]
                   [::uism/asm-id ::navbarsm]]
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults    {:inverted true}
                          merged-data (merge current-normalized data-tree defaults)]
                      merged-data))
   :initial-state {:inverted                 true
                   ::m.navbar/id             :main
                   ::m.navbar/dropdown-links []}}
  (let [authorization (get props [::auth/authorization :local])
        visible       (= (uism/get-active-state this ::navbarsm) :state/shown)
        logged-in?    (= (::auth/status authorization) :success)]
    (dom/div {:classes [:.ui.sidebar.inverted.menu.right.vertical.thin
                        (when visible :.visible)]}
      (if logged-in?
        (comp/fragment
         (map ui-nav-link dropdown-links)
         (ui-navbar-logout-link {}))
        (ui-navbar-login-link {})))))

(def ui-navbar-sidebar (comp/factory NavbarSidebar))

(defsc NavbarAuthQuery
  [_this _props]
  {:ident         ::auth/authorization
   :initial-state {::auth/authorization  :local
                   ::auth/status         :initial
                   :session/current-user {}}
   :query         [::auth/authorization
                   ::auth/status
                   {:session/current-user (comp/get-query NavbarAuthLink)}]})

(defsc Navbar
  [this {::m.navbar/keys [unauth-links] :as props} _ {:keys [navbar]}]
  {:css           [[:.navbar {:background-color "red"}]]
   :ident         ::m.navbar/id
   :initial-state {::expanded?               false
                   ::m.navbar/id             :main
                   ::m.navbar/dropdown-links []
                   ::m.navbar/unauth-links   []}
   :query         [::m.navbar/id
                   {::m.navbar/dropdown-links (comp/get-query NavLink)}
                   {::m.navbar/unauth-links (comp/get-query NavLink)}
                   {[::auth/authorization :local] (comp/get-query NavbarAuthQuery)}
                   ::expanded?
                   [::uism/asm-id ::navbarsm]]}
  (let [authorization (get props [::auth/authorization :local])
        current-user  (:session/current-user authorization)
        inverted      true
        logged-in?    (= (::auth/status authorization) :success)]
    (ui-menu
     {:classes  [navbar]
      :inverted inverted
      :style    {:marginBottom "0"}}
     (ui-menu-item
      {:onClick (fn []
                  (uism/trigger! this auth/machine-id :event/cancel {})
                  (rroute/route-to! this u.home/HomePage {}))
       :style   {:fontWeight :bold}}
      "Dinsro")
     (if logged-in?
       (dom/div :.ui.item
         (ui-navbar-auth-link current-user))
       (map ui-nav-link unauth-links))
     (ui-menu-menu
      {:position "right"}
      (dom/div
        {:classes [:.item]
         :onClick (fn [] (uism/trigger! this ::navbarsm :event/toggle {}))}
        (dom/i :.icon.sidebar))))))

(def ui-navbar (comp/factory Navbar))

(defsc NavbarUnion
  [_this _props]
  {:ident         ::m.navbar/id
   :query         [::m.navbar/id
                   {:root/current-user (comp/get-query NavbarAuthLink)}
                   {::m.navbar/dropdown-links (comp/get-query NavLink)}
                   {::m.navbar/unauth-links (comp/get-query NavLink)}
                   :inverted
                   [::auth/authorization :local]
                   [::uism/asm-id ::navbarsm]]
   :initial-state {::m.navbar/id             :main
                   :inverted                 true
                   :root/current-user        {}
                   ::m.navbar/dropdown-links []
                   ::m.navbar/unauth-links   []}})
