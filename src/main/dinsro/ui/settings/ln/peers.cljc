(ns dinsro.ui.settings.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.peers :as j.ln.peers]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.ui.forms.settings.ln.peers :as u.f.s.ln.peers]
   [dinsro.ui.links :as u.links]))

;; [[../../../../../test/dinsro/ui/settings/ln/peers_test.cljs]]

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this u.f.s.ln.peers/NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.peers/node        #(u.links/ui-node-link %2)
                         ::m.ln.peers/pubkey      #(u.links/ui-ln-peer-link %3)
                         ::m.ln.peers/remote-node #(u.links/ui-remote-node-link %2)}
   ro/columns           [m.ln.peers/node
                         m.ln.peers/remote-node
                         m.ln.peers/inbound?]
   ro/controls          {::new new-button}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.peers/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.peers/index
   ro/title             "Lightning Peers"})

(def ui-report (comp/factory Report))
