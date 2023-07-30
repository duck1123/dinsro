(ns dinsro.ui.admin.core.networks.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   ;; [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../../joins/core/addresses.cljc]]
;; [[../../../../model/core/addresses.cljc]]
;; [[../../../../ui/core/addresses.cljc]]

(def index-page-key :admin-core-networks-show-addresses)
(def model-key ::m.c.addresses/id)
(def parent-model-key ::m.c.networks/id)
(def router-key :dinsro.ui.admin.core.networks/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.addresses/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.addresses/fetch!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.addresses/height #(u.links/ui-block-height-link %3)}
   ro/columns           [m.c.addresses/address]
   ro/control-layout    {:inputs         [[::m.c.networks/id]]
                         :action-buttons [::refresh]}
   ro/controls          {::refresh         u.links/refresh-control
                         ::m.c.networks/id {:type :uuid :label "Nodes"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action
                         delete-action]
   ro/row-pk            m.c.addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.addresses/index
   ro/title             "Addresses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             ::m.navlinks/id
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["addresses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (parent-model-key props)
    (if report
      (ui-report report)
      (u.debug/load-error props "admin network show addresses report"))
    (u.debug/load-error props "admin network show addresses")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Addresses"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-core-networks-show
   ::m.navlinks/router        :admin-core-networks
   ::m.navlinks/required-role :admin})
