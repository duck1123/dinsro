(ns dinsro.ui.nostr.badge-definitions
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-definitions/id
                        m.n.badge-definitions/code
                        m.n.badge-definitions/description
                        m.n.badge-definitions/image-url
                        m.n.badge-definitions/thumbnail-urls]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/route            "badge-definitions"
   ro/row-pk           m.n.badge-definitions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-definitions/index
   ro/title            "Definitions"})
