(ns dinsro.ui.admin.core.networks.addresses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.addresses :as j.c.addresses]
   [dinsro.model.core.addresses :as m.c.addresses]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.addresses :as mu.c.addresses]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../../joins/core/addresses.cljc]]
;; [[../../../../model/core/addresses.cljc]]
;; [[../../../../ui/core/addresses.cljc]]

(def index-page-id :admin-core-networks-show-addresses)
(def model-key ::m.c.addresses/id)
(def parent-model-key ::m.c.networks/id)
(def parent-show-id :admin-core-networks-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.core.networks/Router)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.addresses/delete!))

(def fetch-action
  (u.buttons/row-action-button "Fetch" model-key mu.c.addresses/fetch!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.addresses/height #(u.links/ui-block-height-link %3)}
   ro/columns           [m.c.addresses/address]
   ro/control-layout    {:inputs         [[parent-model-key]]
                         :action-buttons [::refresh]}
   ro/controls          {::refresh         u.links/refresh-control
                         parent-model-key {:type :uuid :label "Id"}}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [fetch-action
                         delete-action]
   ro/row-pk            m.c.addresses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.addresses/admin-index
   ro/title             "Addresses"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             ::m.navlinks/id
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["addresses"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/input-key     parent-model-key
   o.navlinks/label         "Addresses"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-show-id
   o.navlinks/router        parent-show-id
   o.navlinks/required-role required-role})
