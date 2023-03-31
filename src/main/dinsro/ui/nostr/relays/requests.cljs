(ns dinsro.ui.nostr.relays.requests
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.requests :as mu.n.requests]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.n.requests/id
                    m.n.requests/start-time
                    m.n.requests/status
                    m.n.requests/end-time]
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
  {ro/column-formatters {::m.n.requests/code         #(u.links/ui-request-link %3)
                         ::m.n.requests/relay        #(u.links/ui-relay-link %2)
                         ::j.n.requests/filter-count #(u.links/ui-request-filter-count-link %3)
                         ::j.n.requests/run-count    #(u.links/ui-request-run-count-link %3)}
   ro/columns           [m.n.requests/code
                         m.n.requests/relay
                         ;; m.n.requests/status
                         ;; m.n.requests/start-time
                         ;; m.n.requests/end-time
                         j.n.requests/filter-count
                         j.n.requests/run-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.n.relays/id {:type :uuid :label "id"}
                         ::new           new-button
                         ::refresh       u.links/refresh-control}
   ro/row-actions       [(u.links/row-action-button "Run" ::m.n.requests/id mu.n.requests/run!)
                         ;; (u.links/row-action-button "Stop" ::m.n.requests/id mu.n.requests/stop!)
                         ]
   ro/row-pk            m.n.requests/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.requests/index
   ro/title             "Requests"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["requests"]}
  ((comp/factory Report) report))
