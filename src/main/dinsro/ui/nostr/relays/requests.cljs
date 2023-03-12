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
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.mutations.nostr.requests :as mu.n.requests]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.relays/id)
(def router-key :dinsro.ui.nostr.relays/Router)

(defn stop-action
  [report-instance _]
  (let [id (u.links/get-control-value report-instance ::m.n.requests/id)]
    (comp/transact! report-instance [(mu.n.requests/stop! {::m.n.requests/id id})])))

(def stop-action-button
  {:action stop-action
   :label  "Subscribe"})

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
  {:action (fn [this _] (form/create! this NewForm))
   :label  "New"
   :local? true
   :type   :button})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.subscriptions/code #(u.links/ui-subscription-link %3)
                         ::m.n.requests/code      #(u.links/ui-request-link %3)}
   ro/columns           [m.n.requests/code
                         m.n.requests/start-time
                         m.n.requests/status
                         m.n.requests/end-time]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/row-actions       [stop-action-button]
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
