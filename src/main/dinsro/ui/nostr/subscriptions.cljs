(ns dinsro.ui.nostr.subscriptions
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.subscriptions :as j.n.subscriptions]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.subscriptions/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/source-attribute ::j.n.subscriptions/index
   ro/title            "Subscriptions"
   ro/row-pk           m.n.subscriptions/id
   ro/run-on-mount?    true
   ro/route            "subscriptions"})
