(ns dinsro.ui.ln.nodes-test
  (:require
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.client :as client]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(ws/defcard CoreTxForm
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.ln.nodes/CoreTxForm
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] {::m.ln.tx/hash "foo"})}))

(ws/defcard LightningNodeForm
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.ln.nodes/LightningNodeForm
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn []
      {::m.ln.nodes/id           :main
       ::m.ln.nodes/name         "foo"
       ::m.ln.info/alias         "alias"
       ::m.ln.nodes/hasCert?     true
       ::m.ln.nodes/hasMacaroon? true})}))
