(ns dinsro.ui.navbars
  (:require
   #?(:cljs ["semantic-ui-react/dist/commonjs/collections/Menu/Menu" :default Menu])
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar :refer [ui-sidebar]]
   [dinsro.joins.navbars :as j.navbars]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.navbars :as mu.navbars]
   [dinsro.ui.home :as u.home]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../joins/navbars.cljc]]
;; [[../model/navbars.cljc]]
;; [[../mutations/navbars.cljc]]

(def index-page-key :navbars)
(def log-props true)

(defn get-logged-in
  [props]
  (= (get-in props [[::auth/authorization :local] ::auth/status]) :success))

(defsc FormStateConfigQuery
  [_ _]
  {:query         [::fs/id]
   :initial-state {::fs/id nil}})

(defsc FormStateQuery
  [_ _]
  {:query         [{::fs/config (comp/get-query FormStateConfigQuery)}]
   :initial-state {::fs/config {}}})

(defsc RouteTarget
  [_this _props]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id      nil
                   ::m.navlinks/control nil}
   :query         [::m.navlinks/id
                   ::m.navlinks/control]})

(defsc NavLink
  [this {::j.navlinks/keys [path]
         ::m.navlinks/keys [label]
         :as               props}]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id         nil
                   ::m.navlinks/label      ""
                   ::m.navlinks/auth-link? false
                   ::m.navlinks/navigate     {}
                   ::j.navlinks/path       []}
   :query         [::m.navlinks/id
                   ::m.navlinks/label
                   {::m.navlinks/navigate (comp/get-query RouteTarget)}
                   ::m.navlinks/auth-link?
                   ::j.navlinks/path]}
  (log/trace :Navlink/starting {:props props :path path})
  (dom/a :.item
    {:onClick (fn [e]
                (.preventDefault e)
                (let [props (comp/props this)]
                  (log/debug :NavLink/clicked {:props props})
                  (comp/transact! this [`(mu.navbars/navigate! ~props)])))}
    label))

(def ui-nav-link (comp/factory NavLink {:keyfn ::m.navlinks/id}))

(defsc TopNavLink
  [_this {::m.navlinks/keys [children] :as props}]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id       nil
                   ::m.navlinks/label    ""
                   ;; ::j.navlinks/target   {}
                   ::m.navlinks/children []}
   :query         [::m.navlinks/auth-link?
                   ::m.navlinks/id
                   ::m.navlinks/label
                   ;; {::j.navlinks/target (comp/get-query RouteTarget)}
                   {::m.navlinks/children (comp/get-query NavLink)}]}
  (log/debug :top-nav-link/rendered {:props props})
  (if (seq children)
    (dom/div :.ui.simple.dropdown.item
      name
      (dom/i :.dropdown.icon)
      (dom/div :.menu
        (map ui-nav-link children)))
    (ui-nav-link props)))

(def ui-top-nav-link (comp/factory TopNavLink {:keyfn ::m.navlinks/id}))

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
                     (uism/trigger! this ::mu.navbars/navbarsm :event/hide {})
                     false)}
    "Login"))

(def ui-navbar-login-link (comp/factory NavbarLoginLink))

(defsc NavbarLogoutLink
  [this props]
  {:initial-state {}
   :query         []}
  (log/debug :logout-link/rendering {:props props})
  (dom/a :.ui.item.right
    {:onClick (fn [_evt]
                (uism/trigger! this ::mu.navbars/navbarsm :event/hide {})
                (auth/logout! this :local)
                (let [component (comp/registry-key->class :dinsro.ui.home/HomePage)]
                  (rroute/route-to! this component {})))}
    "Logout"))

(def ui-navbar-logout-link (comp/factory NavbarLogoutLink))

(defsc MenuItem
  [_this _props]
  {:initial-state {::m.navbars/id       nil
                   ::m.navbars/children []}
   :ident         ::m.navbars/id
   :query         [::m.navbars/id
                   {::m.navbars/children (comp/get-query NavLink)}]})

(defsc NavbarSidebar
  [this {:ui/keys         [inverted?]
         ::m.navbars/keys [sidebar]
         :as              props}]
  {:ident         ::m.navbars/id
   :query         [::m.navbars/id
                   {::m.navbars/sidebar (comp/get-query MenuItem)}
                   :ui/inverted?
                   [::auth/authorization :local]
                   [::uism/asm-id ::mu.navbars/navbarsm]]
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults    {:ui/inverted? true}
                          merged-data (merge current-normalized data-tree defaults)]
                      (log/info :sidebar/merged {:defaults defaults :merged-data merged-data})
                      merged-data))
   :initial-state {::m.navbars/id      nil
                   ::m.navbars/sidebar {}
                   :ui/inverted?       true}}
  (let [visible?   (= (uism/get-active-state this ::mu.navbars/navbarsm) :state/shown)
        logged-in? (get-logged-in props)]
    (log/debug :sidebar/rendering {:props props :visible? visible?})
    #?(:clj
       (comment inverted? ui-sidebar sidebar logged-in?)
       :cljs
       (ui-sidebar
        {:direction "left"
         :as        Menu
         :animation "overlay"
         :inverted  inverted?
         :vertical  true
         :width     "thin"
         :visible   visible?}
        (if logged-in?
          (comp/fragment
           (map ui-nav-link (::m.navbars/children sidebar))
           (ui-navbar-logout-link {}))
          (ui-navbar-login-link {}))))))

(def ui-navbar-sidebar (comp/factory NavbarSidebar))

(defsc LogoutNavLink
  [this _props]
  {}
  (dom/a :.item.right
    {:onClick (fn [_evt]
                (uism/trigger! this ::mu.navbars/navbarsm :event/hide {})
                (auth/logout! this :local)
                (let [component (comp/registry-key->class :dinsro.ui.home/HomePage)]
                  (rroute/route-to! this component {})))}
    "Logout"))

(def ui-logout-nav-link (comp/factory LogoutNavLink))

(defsc NavbarAuthQuery
  [_this _props]
  {:ident         ::auth/authorization
   :initial-state {::auth/authorization :local
                   ::auth/status        :initial}
   :query         [::auth/authorization
                   ::auth/status]})

(defsc SiteButton
  [this _props]
  {:css           [[:.site-button {:font-weight "bold"
                                   :color       "blue !important"}]]
   :query         []
   :initial-state {}
   :ident         (fn [_] [:component/id ::SiteButton])}
  (let [{:keys [site-button]} (css/get-classnames SiteButton)]
    (dom/a :.item
      {:classes [:.item site-button]
       :onClick (fn []
                  (uism/trigger! this auth/machine-id :event/cancel {})
                  (rroute/route-to! this u.home/Page {}))}
      "dinsro")))

(def ui-site-button (comp/factory SiteButton))

(defsc MinimalNavbar
  [this {:ui/keys [site-button] :as props}]
  {:css           [[:.navbar {:background-color "red"}]]
   :ident         ::m.navbars/id
   :initial-state {::m.navbars/id   :main
                   :ui/expanded?    false
                   :ui/menu-links   []
                   :ui/site-button  {}
                   :ui/unauth-links []}
   :query         [{[::auth/authorization :local] (comp/get-query NavbarAuthQuery)}
                   ::m.navbars/id
                   :ui/expanded?
                   {:ui/menu-links (comp/get-query TopNavLink)}
                   {:ui/site-button (comp/get-query SiteButton)}
                   {:ui/unauth-links (comp/get-query NavLink)}
                   [::uism/asm-id ::mu.navbars/navbarsm]]}
  (let [inverted   true
        logged-in? (get-logged-in props)]
    (log/trace :navbar/rendering {:inverted inverted :logged-in? logged-in?})
    (dom/div {:classes [:.ui.top.menu.fixed (when inverted :.inverted)]}
      (ui-menu-menu {}
        (dom/div {:classes [:.item]
                  :onClick (fn [] (uism/trigger! this ::mu.navbars/navbarsm :event/toggle {}))}
          (dom/i :.icon.sidebar)))
      (ui-site-button site-button))))

(def ui-minimal-navbar (comp/factory MinimalNavbar))

(defsc Navbar
  [_this {::m.navbars/keys [authenticated unauthenticated]
          :ui/keys         [inverted? site-button]
          :as              props}]
  {:css           [[:.navbar {:background-color "red"}]]
   :ident         ::m.navbars/id
   :initial-state {::m.navbars/authenticated   {}
                   ::m.navbars/id              nil
                   ::m.navbars/sidebar         {}
                   ::m.navbars/unauthenticated {}
                   :ui/expanded?               false
                   :ui/inverted?               true
                   :ui/site-button             {}}
   :pre-merge     (fn [{:keys [current-normalized data-tree]}]
                    (let [defaults {:ui/inverted?   true
                                    :ui/site-button (comp/get-initial-state SiteButton)}]
                      (merge current-normalized data-tree defaults)))
   :query         [:ui/inverted?
                   {::m.navbars/authenticated (comp/get-query MenuItem)}
                   {::m.navbars/unauthenticated (comp/get-query MenuItem)}
                   {::m.navbars/sidebar (comp/get-query MenuItem)}
                   ::m.navbars/id
                   {:ui/site-button (comp/get-query SiteButton)}
                   {[::auth/authorization :local] (comp/get-query NavbarAuthQuery)}
                   :ui/expanded?
                   [::uism/asm-id ::mu.navbars/navbarsm]]}
  (log/trace :Navbar/starting {:props props})
  (let [logged-in? (get-logged-in props)
        links      (::m.navbars/children (if logged-in? authenticated unauthenticated))]
    (log/trace :navbar/rendering {:inverted? inverted? :logged-in? logged-in?})
    (ui-menu {:fixed "top" :inverted true}
      (ui-site-button site-button)
      (map ui-top-nav-link links)
      (when logged-in?
        (ui-logout-nav-link {})))))

(def ui-navbar (comp/factory Navbar))

(report/defsc-report Report
  [_this _props]
  {ro/columns             [m.navbars/id
                           m.navbars/parent
                           m.navbars/child-count]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          ::m.navbars/date
                           :sortable-columns #{::m.navbars/date}
                           :ascending?       false}
   ro/row-pk              m.navbars/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.navbars/index
   ro/title               "Navbars"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [_] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   ::m.navlinks/id    :navbars
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["navbars"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/trace :Page/starting {:props props})
  (ui-report report))

(m.navlinks/defroute   :navbars
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Navbars"
   ::m.navlinks/model-key     ::m.navbars/id
   ::m.navlinks/parent-key    :root
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :user})
