(ns dinsro.ui.nostr.relays.requests
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.requests :as mu.n.requests]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :nostr-relays-show-requests)
(def model-key ::m.n.requests/id)
(def parent-model-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(def run-action
  (u.buttons/row-action-button "Run" model-key mu.n.requests/run!))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.n.requests/id
                    m.n.requests/code]
   fo/cancel-route ["requests"]
   fo/id           m.n.requests/id
   fo/route-prefix "new-request"
   fo/title        "Create Request"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this NewForm))})

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
   :route-segment     ["requests"]}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Requests"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-relays-show
   ::m.navlinks/router        :nostr-relays
   ::m.navlinks/required-role :user})
