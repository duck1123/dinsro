(ns dinsro.ui.admin.nostr.relays.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/nostr/witnesses.cljc]]
;; [[../../../../model/nostr/witnesses.cljc]]
;; [[../../../../ui/admin/nostr/witnesses.cljc]]

(def index-page-key :admin-nostr-relays-show-witnesses)
(def model-key ::m.n.witnesses/id)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.admin.nostr.relays/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.witnesses/id #(u.links/ui-admin-witness-link %3)}
   ro/columns           [m.n.witnesses/id]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.witnesses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.witnesses/admin-index
   ro/title             "Witnesses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["witnesses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin relay witnesses report"))
    (u.debug/load-error props "admin relay witnesses page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Witnesses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-nostr-relays-show
   ::m.navlinks/router        :admin-nostr-relays
   ::m.navlinks/required-role :admin})
