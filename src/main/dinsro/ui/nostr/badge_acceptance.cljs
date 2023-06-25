(ns dinsro.ui.nostr.badge-acceptance
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.badge-acceptances :as j.n.badge-acceptances]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/badge_acceptances.cljc]]
;; [[../../model/nostr/badge_acceptances.cljc]]

(def index-page-key :nostr-badge-acceptances)
(def model-link ::m.n.badge-acceptances/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-acceptances/id]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.badge-acceptances/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-acceptances/index
   ro/title            "Definitions"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["badge-acceptances"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (dom/div :.ui.segment "Failed to load report"))))
