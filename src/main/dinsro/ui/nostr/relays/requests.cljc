(ns dinsro.ui.nostr.relays.requests
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.requests :as mu.n.requests]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.forms.nostr.relays.requests :as u.f.n.r.requests]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

(def index-page-id :nostr-relays-show-requests)
(def model-key ::m.n.requests/id)
(def parent-model-key ::m.n.relays/id)
(def parent-router-id :nostr-relays-show)
(def required-role :user)
(def router-key :dinsro.ui.nostr.relays/Router)

(def run-action
  (u.buttons/row-action-button "Run" model-key mu.n.requests/run!))

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.n.r.requests/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.requests/code         #(u.links/ui-admin-request-link %3)
                         ::m.n.requests/relay        #(u.links/ui-admin-relay-link %2)
                         ::j.n.requests/filter-count #(u.links/ui-admin-request-filter-count-link %3)
                         ::j.n.requests/run-count    #(u.links/ui-admin-request-run-count-link %3)}
   ro/columns           [m.n.requests/code
                         m.n.requests/relay
                         j.n.requests/filter-count
                         j.n.requests/run-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::new            new-button
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [run-action]
   ro/row-pk            m.n.requests/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.requests/index
   ro/title             "Requests"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-id
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["requests"]}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Requests"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
