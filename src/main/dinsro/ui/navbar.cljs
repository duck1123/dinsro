(ns dinsro.ui.navbar
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.elements.icon.ui-icon :refer [ui-icon]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-item :refer [ui-menu-item]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar :refer [ui-sidebar]]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.home :as u.home]
   ["semantic-ui-react/dist/commonjs/collections/Menu/Menu" :default Menu]
   [taoensso.timbre :as log]))

(s/def ::expanded? boolean?)

(defsc NavLink
  [this {::m.navlink/keys [name target]}]
  {:ident         ::m.navlink/id
   :initial-state {::m.navlink/id     nil
                   ::m.navlink/name   ""
                   ::m.navlink/target nil}
   :query         [::m.navlink/id
                   ::m.navlink/name
                   ::m.navlink/target]}
  (dom/a :.ui.item
    {:onClick (fn [_e]
                (if-let [component (comp/registry-key->class target)]
                  (do
                    (uism/trigger! this ::navbarsm :event/hide {})
                    (rroute/route-to! this component {}))
                  (log/infof "Could note find target: %s" target)))}
    name))

(def ui-nav-link (comp/factory NavLink {:keyfn ::m.navlink/id}))

(defsc NavbarAuthLink
  [this {::m.users/keys [name id]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   nil
                   ::m.users/name ""}
   :query         [::m.users/id
                   ::m.users/name]}
  (let [component (comp/registry-key->class :dinsro.ui.show-user/ShowUserPage)]
    (dom/a :.navbar-link
      {:onClick (fn [_e] (rroute/route-to! this component {::m.users/id id}))}
      name)))

(defsc NavbarLoginLink
  [this _]
  {:initial-state {}
   :query         []}
  (dom/a :.ui.item
    {:onClick (fn [evt]
                (let [component (comp/registry-key->class :dinsro.ui.login/LoginPage)]
                  (rroute/route-to! this component {}))
                (.preventDefault evt)
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
                (auth/logout! this :local))}
    "Logout"))

(def ui-navbar-logout-link (comp/factory NavbarLogoutLink))

(defsc NavbarSidebar
  [this {:keys            [inverted]
         ::m.navlink/keys [dropdown-links]
         :as              props}]
  {:ident         :navbar/id
   :query         [:navbar/id
                   {::m.navlink/dropdown-links (comp/get-query NavLink)}
                   :inverted
                   [::auth/authorization :local]
                   [::uism/asm-id ::navbarsm]]
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults    {:inverted true}
                          merged-data (merge current-normalized data-tree defaults)]
                      merged-data))
   :initial-state {:inverted                  true
                   :navbar/id                 :main
                   ::m.navlink/dropdown-links []}}
  (let [authorization (get props [::auth/authorization :local])
        visible       (= (uism/get-active-state this ::navbarsm) :state/shown)
        logged-in?    (= (::auth/status authorization) :success)]
    (ui-sidebar
     {:as        Menu
      :animation "overlay"
      :direction "right"
      :inverted  inverted
      :vertical  true
      :visible   visible}
     (if logged-in?
       (comp/fragment
        (map ui-nav-link dropdown-links)
        (ui-navbar-logout-link {}))
       (ui-navbar-login-link {})))))

(def ui-navbar-sidebar (comp/factory NavbarSidebar))

(defsc Navbar
  [this {::m.navlink/keys [unauth-links] :as props}]
  {:css           [[:.navbar {:background-color "red"}]]
   :ident         :navbar/id
   :initial-state {::expanded?               false
                   :navbar/id                :main
                   ::m.navlink/menu-links    []
                   ::m.navlink/unauth-links  []
                   :session/current-user-ref {}
                   :authenticator            {}}
   :query         [:navbar/id
                   [::auth/authorization :local]
                   {:authenticator (comp/get-query u.authenticator/Authenticator)}
                   {:session/current-user-ref (comp/get-query NavbarAuthLink)}
                   ::expanded?
                   {::m.navlink/menu-links (comp/get-query NavLink)}
                   {::m.navlink/unauth-links (comp/get-query NavLink)}]}
  (let [authorization (get props [::auth/authorization :local])
        inverted      true
        logged-in?    (= (::auth/status authorization) :success)
        user-ref      (:session/current-user-ref authorization)
        user-id       (and user-ref (second user-ref))
        name          (::m.users/name authorization)]
    (ui-menu
     {:inverted inverted
      :style    {:marginBottom "0"}}
     (ui-menu-item
      {:onClick #(rroute/route-to! this u.home/HomePage {})
       :style   {:fontWeight :bold}}
      "Dinsro")
     (if logged-in?
       (dom/div :.ui.item
         (dom/a {:onClick (fn [_e] (log/infof "clicked: %s" (str user-id)))}
           name))
       (map ui-nav-link unauth-links))
     (ui-menu-menu
      {:position "right"}
      (ui-menu-item
       {:onClick #(uism/trigger! this ::navbarsm :event/toggle {})}
       (ui-icon {:name "sidebar"}))))))

(def ui-navbar (comp/factory Navbar))
