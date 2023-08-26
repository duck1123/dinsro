(ns dinsro.ui.nostr.badge-acceptance
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.badge-acceptances :as j.n.badge-acceptances]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/badge_acceptances.cljc]]
;; [[../../model/nostr/badge_acceptances.cljc]]

(def index-page-id :nostr-badge-acceptances)
(def model-key ::m.n.badge-acceptances/id)
(def parent-router-id :nostr)
(def required-role :user)

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
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["badge-acceptances"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (ui-segment {}
        "Failed to load report"))))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Badge Acceptances"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
