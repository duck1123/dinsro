(ns dinsro.ui.nostr.relays
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.relays.events :as u.n.r.events]
   [dinsro.ui.nostr.relays.pubkeys :as u.n.r.pubkeys]
   [dinsro.ui.nostr.relays.requests :as u.n.r.requests]
   [dinsro.ui.nostr.relays.subscriptions :as u.n.r.subscriptions]
   [dinsro.ui.nostr.relays.topics :as u.n.r.topics]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../mutations/nostr/relays.cljc][Mutations]]
;; [[../../queries/nostr/relays.clj][Queries]]

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _]
             (let [props (comp/props this)
                   address (::m.n.relays/address props)]
               (log/info :submit-button/clicked {:address address})
               (comp/transact!
                this
                [(mu.n.relays/submit!
                  {::m.n.relays/address address})])))})

(form/defsc-form NewRelayForm [_this _props]
  {fo/id             m.n.relays/id
   fo/action-buttons [::submit]
   fo/attributes     [m.n.relays/address]
   fo/cancel-route   ["relays"]
   fo/controls       {::submit submit-button}
   fo/route-prefix   "new-relay"
   fo/title          "Relay"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Relay"
   :action (fn [this _] (form/create! this NewRelayForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.relays/address #(u.links/ui-relay-link %3)}
   ro/columns           [m.n.relays/address
                         m.n.relays/connected
                         j.n.relays/subscription-count
                         j.n.relays/request-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/route             "relays"
   ro/row-actions       [(u.links/row-action-button "Fetch" ::m.n.relays/id mu.n.relays/fetch!)
                         (u.links/row-action-button "Toggle" ::m.n.relays/id mu.n.relays/toggle!)
                         (u.links/row-action-button "Delete" ::m.n.relays/id mu.n.relays/delete!)]
   ro/row-pk            m.n.relays/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays Report"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.r.pubkeys/SubPage
    u.n.r.subscriptions/SubPage
    u.n.r.topics/SubPage
    u.n.r.requests/SubPage
    u.n.r.events/SubPage]})

(def menu-items
  [{:key   "requests"
    :name  "Requests"
    :route "dinsro.ui.nostr.relays.requests/SubPage"}
   {:key   "events"
    :name  "Events"
    :route "dinsro.ui.nostr.relays.events/SubPage"}
   {:key   "subscriptions"
    :name  "Subscriptions"
    :route "dinsro.ui.nostr.relays.subscriptions/SubPage"}
   {:key   "pubkeys"
    :name  "Pubkeys"
    :route "dinsro.ui.nostr.relays.pubkeys/SubPage"}
   {:key   "topics"
    :name  "Topics"
    :route "dinsro.ui.nostr.relays.topics/SubPage"}])

(defsc Show
  [this {::m.n.relays/keys [id address connected]
         :ui/keys          [router]}]
  {:ident         ::m.n.relays/id
   :initial-state {::m.n.relays/id        nil
                   ::m.n.relays/address   ""
                   ::m.n.relays/connected false
                   :ui/router             {}}
   :pre-merge     (u.links/page-merger ::m.n.relays/id {:ui/router Router})
   :query         [::m.n.relays/id
                   ::m.n.relays/address
                   ::m.n.relays/connected
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["relay" :id]
   :will-enter    (partial u.links/page-loader ::m.n.relays/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/dl {}
          (dom/dt {} "Address")
          (dom/dd {} (str address))
          (dom/dt {} "Connected")
          (dom/dd {} (str connected)))
        (dom/button {:classes [:.ui.button]
                     :onClick (fn [_e]
                                (log/info :click {})
                                (comp/transact! this [(mu.n.relays/fetch! {::m.n.relays/id id})]))}

          "Fetch")
        (dom/button {:classes [:.ui.button]
                     :onClick (fn [_e]
                                (log/info :click {})
                                (comp/transact! this [(mu.n.relays/toggle! {::m.n.relays/id id})]))}

          "Toggle"))
      (u.links/ui-nav-menu {:menu-items menu-items :id id})
      ((comp/factory Router) router))))
