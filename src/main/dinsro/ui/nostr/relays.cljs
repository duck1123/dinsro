(ns dinsro.ui.nostr.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defn delete-action
  [report-instance {::m.n.relays/keys [id]}]
  (form/delete! report-instance ::m.n.relays/id id))

(defn fetch-action
  [report-instance {::m.n.relays/keys [id]}]
  (comp/transact! report-instance [(mu.n.relays/fetch! {::m.n.relays/id id})]))

(defn connect-action
  [report-instance {::m.n.relays/keys [id] :as props}]
  (log/info :connect-action/starting {:props props})
  (if id
    (comp/transact! report-instance [(mu.n.relays/connect! {::m.n.relays/id id})])
    (throw (js/Error. "no id"))))

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(def fetch-action-button
  {:label  "Fetch"
   :action fetch-action
   :style  :fetch-button})

(def connect-action-button
  {:label  "Connect"
   :action connect-action
   :style  :connect-button})

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
  {ro/columns           [m.n.relays/id
                         m.n.relays/address
                         m.n.relays/connected]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/row-actions       [fetch-action-button
                         connect-action-button
                         delete-action-button]
   ro/source-attribute  ::m.n.relays/index
   ro/title             "Relays Report"
   ro/row-pk            m.c.nodes/id
   ro/run-on-mount?     true
   ro/route             "relays"})
