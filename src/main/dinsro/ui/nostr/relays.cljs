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
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.relay-pubkeys :as u.n.relay-pubkeys]
   [dinsro.ui.nostr.relay-subscriptions :as u.n.relay-subscriptions]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj][Actions]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../mutations/nostr/relays.cljc][Mutations]]
;; [[../../queries/nostr/relays.clj][Queries]]

(defn delete-action
  [report-instance {::m.n.relays/keys [id]}]
  (form/delete! report-instance ::m.n.relays/id id))

(defn fetch-action
  [report-instance {::m.n.relays/keys [id]}]
  (comp/transact! report-instance [(mu.n.relays/fetch! {::m.n.relays/id id})]))

(defn toggle-action
  [report-instance {::m.n.relays/keys [id] :as props}]
  (log/info :connect-action/starting {:props props})
  (if id
    (comp/transact! report-instance [(mu.n.relays/toggle! {::m.n.relays/id id})])
    (throw (js/Error. "no id"))))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(def fetch-action-button
  {:label  "Fetch"
   :action fetch-action
   :style  :fetch-button})

(def toggle-action-button
  {:label  "Toggle"
   :action toggle-action
   :style  :toggle-button})

(form/defsc-form NewRelayForm [_this _props]
  {fo/id           m.n.relays/id
   fo/attributes   [m.n.relays/address]
   fo/cancel-route ["relays"]
   fo/route-prefix "new-relay"
   fo/title        "Relay"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Relay"
   :action (fn [this _] (form/create! this NewRelayForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.relays/address #(u.links/ui-relay-link %3)}
   ro/columns           [m.n.relays/id
                         m.n.relays/address
                         m.n.relays/connected]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/route             "relays"
   ro/row-actions       [fetch-action-button
                         toggle-action-button
                         delete-action-button]
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.relays/index
   ro/title             "Relays Report"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.relay-pubkeys/AddForm
    u.n.relay-pubkeys/SubPage
    u.n.relay-subscriptions/SubPage]})

(def menu-items
  [{:key   "subscriptions"
    :name  "Subscriptions"
    :route "dinsro.ui.nostr.relay-subscriptions/SubPage"}
   {:key   "pubkeys"
    :name  "Pubkeys"
    :route "dinsro.ui.nostr.relay-pubkeys/SubPage"}])

(defsc Show
  [this {::m.n.relays/keys [id address]
         :ui/keys          [router]}]
  {:ident         ::m.n.relays/id
   :initial-state {::m.n.relays/id      nil
                   ::m.n.relays/address ""
                   :ui/router           {}}
   :pre-merge     (u.links/page-merger ::m.n.relays/id {:ui/router Router})
   :query         [::m.n.relays/id
                   ::m.n.relays/address
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["relay" :id]
   :will-enter    (partial u.links/page-loader ::m.n.relays/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/dl {}
          (dom/dt {} "Address")
          (dom/dd {} (str address)))
        (dom/button {:classes [:.ui.button]
                     :onClick (fn [_e]
                                (log/info :click {})
                                (comp/transact! this [(mu.n.relays/fetch! {::m.n.relays/id id})]))}

          "Fetch"))
      (u.links/ui-nav-menu {:menu-items menu-items :id id})
      ((comp/factory Router) router))))
