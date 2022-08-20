(ns dinsro.ui.navbar
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar :refer [ui-sidebar]]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.navbar :as mu.navbar]
   [dinsro.ui.home :as u.home]
   [lambdaisland.glogc :as log]
   ["semantic-ui-react/dist/commonjs/collections/Menu/Menu" :default Menu]))

(s/def ::expanded? boolean?)

(defsc FormStateConfigQuery
  [_ _]
  {:query         [::fs/id]
   :initial-state {::fs/id nil}})

(defsc FormStateQuery
  [_ _]
  {:query         [{::fs/config (comp/get-query FormStateConfigQuery)}]
   :initial-state {::fs/config {}}})

(defsc RouteQuery
  [_ _]
  {:query         [{::dr/current-route (comp/get-query FormStateQuery)}]
   :initial-state {::dr/current-route {}}})

(defsc NavLink
  [this {::m.navlink/keys [name] :as props}]
  {:ident         ::m.navlink/id
   :initial-state {::m.navlink/id         nil
                   ::m.navlink/name       ""
                   ::m.navlink/auth-link? false
                   ::m.navlink/target     nil
                   :root/router           {}}
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults {:root/router (comp/get-initial-state RouteQuery)}]
                      (merge current-normalized data-tree defaults)))
   :query         [::m.navlink/auth-link?
                   ::m.navlink/id
                   ::m.navlink/name
                   ::m.navlink/target
                   {[:root/router '_] (comp/get-query RouteQuery)}]}
  (log/fine :Navlink/starting {:props props})
  (dom/a :.item
    {:onClick (fn [e]
                (.preventDefault e)
                (let [props (comp/props this)]
                  (log/info :NavLink/clicked {:props props})
                  (comp/transact! this [(mu.navbar/navigate! props)])))}
    name))

(def ui-nav-link (comp/factory NavLink {:keyfn ::m.navlink/id}))

(defsc TopNavLink
  [_this {::m.navlink/keys [name children] :as props}]
  {:ident         ::m.navlink/id
   :initial-state {::m.navlink/id         nil
                   ::m.navlink/name       ""
                   ::m.navlink/auth-link? false
                   ::m.navlink/target     nil
                   ::m.navlink/children   []
                   :root/router           {}}
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults {:root/router (comp/get-initial-state RouteQuery)}]
                      (merge current-normalized data-tree defaults)))
   :query         [::m.navlink/auth-link?
                   ::m.navlink/id
                   ::m.navlink/name
                   ::m.navlink/target
                   {::m.navlink/children (comp/get-query NavLink)}
                   {[:root/router '_] (comp/get-query RouteQuery)}]}
  (log/debug :top-nav-link/rendered {:props props})
  (if (seq children)
    (dom/div :.ui.simple.dropdown.item
      name
      (dom/i :.dropdown.icon)
      (dom/div :.menu
        (map ui-nav-link children)))
    (ui-nav-link props)))

(def ui-top-nav-link (comp/factory TopNavLink {:keyfn ::m.navlink/id}))

(defsc NavbarAuthLink
  [this {::m.users/keys [name id]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   nil
                   ::m.users/name ""}
   :query         [::m.users/id
                   ::m.users/name]}
  (let [component (comp/registry-key->class :dinsro.ui.users/ShowUser)]
    (dom/a :.ui.item
      {:onClick (fn [_e] (form/view! this component id))}
      name)))

(def ui-navbar-auth-link (comp/factory NavbarAuthLink))

(defsc NavbarLoginLink
  [this _ _ {:keys [red]}]
  {:initial-state {}
   :query         []
   :css           [[:.red {:color    "red"
                           :fontSize "large"}]]}
  (dom/a {:classes [:.ui.item red]
          :onClick (fn [evt]
                     (.preventDefault evt)
                     (auth/authenticate! this :local nil)
                     (uism/trigger! this ::mu.navbar/navbarsm :event/hide {})
                     false)}
    "Login"))

(def ui-navbar-login-link (comp/factory NavbarLoginLink))

(defsc NavbarLogoutLink
  [this props]
  {:initial-state {}
   :query         []}
  (log/debug :logout-link/rendering {:props props})
  (dom/a :.ui.item
    {:onClick (fn [_evt]
                (uism/trigger! this ::mu.navbar/navbarsm :event/hide {})
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
                   [::uism/asm-id ::mu.navbar/navbarsm]]
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults    {:inverted true}
                          merged-data (merge current-normalized data-tree defaults)]
                      (log/info :sidebar/merged {:defaults defaults :merged-data merged-data})
                      merged-data))
   :initial-state {:inverted                 true
                   ::m.navbar/id             :main
                   ::m.navbar/dropdown-links []}}
  (let [authorization (get props [::auth/authorization :local])
        visible       (= (uism/get-active-state this ::mu.navbar/navbarsm) :state/shown)
        logged-in?    (= (::auth/status authorization) :success)]
    (log/debug :sidebar/rendering {:props props :visible visible})
    (ui-sidebar
     {:direction "right"
      :as        Menu
      :animation "overlay"
      :inverted  true
      :vertical  true
      :width     "thin"
      :visible   visible}
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
  [this {::m.navbar/keys [menu-links unauth-links] :as props}]
  {:css           [[:.navbar {:background-color "red"}]
                   [:.site-button {:font-weight "bold"
                                   :color       "blue !important"}]]
   :ident         ::m.navbar/id
   :initial-state {::expanded?               false
                   ::m.navbar/id             :main
                   ::m.navbar/dropdown-links []
                   ::m.navbar/menu-links     []
                   ::m.navbar/unauth-links   []}
   :query         [::m.navbar/id
                   {::m.navbar/dropdown-links (comp/get-query NavLink)}
                   {::m.navbar/menu-links (comp/get-query TopNavLink)}
                   {::m.navbar/unauth-links (comp/get-query NavLink)}
                   {[::auth/authorization :local] (comp/get-query NavbarAuthQuery)}
                   ::expanded?
                   [::uism/asm-id ::mu.navbar/navbarsm]]}
  (log/finest :navbar/pre-rendering {:props props})
  (let [{:keys [site-button]} (css/get-classnames Navbar)
        authorization         (get props [::auth/authorization :local])
        current-user          (:session/current-user authorization)
        inverted              true
        logged-in?            (= (::auth/status authorization) :success)]

    (log/debug :navbar/rendering {:authorization authorization
                                  :current-user  current-user
                                  :inverted      inverted
                                  :logged-in?    logged-in?})

    (dom/div {:classes [:.ui.top.menu (when inverted :.inverted)]}
      (dom/a :.item
        {:classes [:.item site-button]
         :onClick (fn []
                    (uism/trigger! this auth/machine-id :event/cancel {})
                    (rroute/route-to! this u.home/HomePage {}))}
        "dinsro")
      (if logged-in?
        (comp/fragment
         (ui-navbar-auth-link current-user)
         (map ui-top-nav-link menu-links))
        (map ui-top-nav-link unauth-links))
      (ui-menu-menu
       {:position "right"}
       (dom/div {:classes [:.item]
                 :onClick (fn [] (uism/trigger! this ::mu.navbar/navbarsm :event/toggle {}))}
         (dom/i :.icon.sidebar))))))

(def ui-navbar (comp/factory Navbar))

(defsc NavbarUnion
  [_this _props]
  {:ident         ::m.navbar/id
   :query         [::m.navbar/id
                   {:root/current-user (comp/get-query NavbarAuthLink)}
                   {::m.navbar/dropdown-links (comp/get-query NavLink)}
                   {::m.navbar/unauth-links (comp/get-query NavLink)}
                   {::m.navbar/menu-links (comp/get-query TopNavLink)}
                   :inverted
                   [::auth/authorization :local]
                   [::uism/asm-id ::mu.navbar/navbarsm]]
   :initial-state {::m.navbar/id             :main
                   :inverted                 true
                   :root/current-user        {}
                   ::m.navbar/dropdown-links []
                   ::m.navbar/unauth-links   []
                   ::m.navbar/menu-links     []}})
