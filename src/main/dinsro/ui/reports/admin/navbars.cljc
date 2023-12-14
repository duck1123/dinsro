(ns dinsro.ui.reports.admin.navbars
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.navbars :as j.navbars]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns             [m.navbars/id
                           m.navbars/parent
                           m.navbars/child-count]
   ro/control-layout      {:action-buttons [::refresh]}
   ro/controls            {::refresh u.links/refresh-control}
   ro/initial-sort-params {:sort-by          o.navbars/id
                           :sortable-columns #{o.navbars/id}
                           :ascending?       false}
   ro/row-pk              m.navbars/id
   ro/run-on-mount?       true
   ro/source-attribute    ::j.navbars/admin-index
   ro/title               "Navbars"})

(def ui-report (comp/factory Report))
