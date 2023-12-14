(ns dinsro.ui.admin.navbars
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.admin.navbars :as u.r.a.navbars]
   [lambdaisland.glogc :as log]))

(def index-page-id :admin-navbars)
(def model-key o.navbars/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-id :admin-navbars-show)

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % u.r.a.navbars/Report {})
   :ident             (fn [_] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state u.r.a.navbars/Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query u.r.a.navbars/Report)}])
   :route-segment     ["navbars"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/trace :Page/starting {:props props})
  (u.r.a.navbars/ui-report report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Navbars"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/description   "Admin show page for navbars"
   o.navlinks/label         "Show Navbar"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
