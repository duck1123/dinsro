(ns dinsro.ui.ln.nodes-test
  (:require
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.ln.nodes :as u.ln.nodes]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(ws/defcard LightningNodeForm
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.ln.nodes/LightningNodeForm
    ::ct.fulcro3/initial-state
    (fn [] {::m.ln.nodes/id :main})}))
