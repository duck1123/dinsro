(ns dinsro.ui.admin.navbars
  (:require
   ;; #?(:cljs ["semantic-ui-react/dist/commonjs/collections/Menu/Menu" :default Menu])
   ;; [com.fulcrologic.fulcro-css.css :as css]
   ;; [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   ;; #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   ;; #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   ;; [com.fulcrologic.fulcro.ui-state-machines :as uism]
   ;; [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   ;; [com.fulcrologic.rad.routing :as rroute]
   ;; [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   ;; [com.fulcrologic.semantic-ui.collections.menu.ui-menu-menu :refer [ui-menu-menu]]
   ;; [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar :refer [ui-sidebar]]
   [dinsro.joins.navbars :as j.navbars]
   ;; [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   ;; [dinsro.mutations.navbars :as mu.navbars]
   ;; [dinsro.ui.home :as u.home]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :admin-navbars)
(def model-key ::m.navbars/id)
(def parent-router :root)
(def show-page-key :admin-navbars-show)

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
   ro/source-attribute    ::j.navbars/admin-index
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

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Navbars"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :root
   ::m.navlinks/router        :root
   ::m.navlinks/required-role :user})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin show page for navbars"
   ::m.navlinks/label         "Show Navbar"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
