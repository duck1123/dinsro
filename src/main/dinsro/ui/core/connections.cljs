(ns dinsro.ui.core.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.connections :as j.c.connections]
   [dinsro.model.core.connections :as m.c.connections]
   [dinsro.mutations.core.connections :as mu.c.connections]))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.c.connections/host
                    m.c.connections/port
                    m.c.connections/rpcuser
                    m.c.connections/rpcpass]
   fo/cancel-route ["connections"]
   fo/id           m.c.connections/id
   fo/route-prefix "new-connection"
   fo/title        "Core Node Connection"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Connection"
   :action (fn [this {::m.c.connections/keys [id]}]
             (comp/transact! this [(mu.c.connections/create! {::m.c.connections/id id})]))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.connections/host
                        m.c.connections/port]
   ro/control-layout   {:action-buttons [::new]}
   ro/controls         {::new new-button}
   ro/route            "connections"
   ro/row-pk           m.c.connections/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.connections/index
   ro/title            "Core Node Connections"})
