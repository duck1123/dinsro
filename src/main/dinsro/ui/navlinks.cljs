(ns dinsro.ui.navlinks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [[../joins/navlinks.cljc]]
;; [[../model/navlinks.cljc]]
;; [[../mutations/navlinks.cljc]]

(def index-page-key :navlinks)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters   {::m.navlinks/route     #(str %2)
                           ::m.navlinks/router    #(and %2 (u.links/ui-navbar-link %2))
                           ::m.navlinks/model-key #(str %2)
                           ::m.navlinks/input-key #(str %2)}
   ro/columns             [m.navlinks/id
                           m.navlinks/label
                           m.navlinks/description
                           m.navlinks/router
                           m.navlinks/route
                           m.navlinks/model-key
                           m.navlinks/input-key
                           m.navlinks/auth-link?
                           m.navlinks/required-role]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          ::m.navlinks/label
                           :sortable-columns #{::m.navlinks/label}
                           :ascending?       false}
   ro/row-pk              m.navlinks/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.navlinks/index
   ro/title               "Navlinks"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report] :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [_] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["navlinks"]}
  (log/trace :Page/starting {:props props})
  (ui-report report))
