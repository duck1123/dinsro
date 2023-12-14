(ns dinsro.ui.reports.admin.navlinks
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters   {o.navlinks/id           #(str %2)
                           o.navlinks/control      #(str %2)
                           o.navlinks/router       #(and %2 (u.links/ui-navbar-link %2))
                           o.navlinks/model-key    #(str %2)
                           o.navlinks/navigate-key #(str %2)
                           o.navlinks/parent-key   #(str %2)
                           o.navlinks/input-key    #(str %2)}
   ro/columns             [m.navlinks/label
                           m.navlinks/id
                           m.navlinks/parent-key
                           m.navlinks/description
                           m.navlinks/navigate-key
                           m.navlinks/input-key
                           m.navlinks/required-role]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          o.navlinks/control
                           :sortable-columns #{o.navlinks/label
                                               o.navlinks/parent-key
                                               o.navlinks/control}
                           :ascending?       false}
   ro/row-pk              m.navlinks/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.navlinks/index
   ro/title               "Navlinks"})

(def ui-report (comp/factory Report))
