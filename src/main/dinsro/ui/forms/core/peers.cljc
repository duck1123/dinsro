(ns dinsro.ui.forms.core.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.peers :as m.c.peers]
   [dinsro.mutations.core.peers :as mu.c.peers]
   [dinsro.options.core.peers :as o.c.peers]
   [dinsro.ui.pickers :as u.pickers]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.c.peers/keys [addr id]
                    node             ::m.c.peers/node} (comp/props this)
                   {node-id ::m.c.nodes/id}            node
                   props                               {o.c.peers/id   id
                                                        o.c.peers/addr addr
                                                        o.c.peers/node node-id}]
               (comp/transact! this [`(mu.c.peers/create! ~props)])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.c.peers/addr
                      m.c.peers/node]
   fo/controls       {::submit submit-button}
   fo/field-options  {::m.c.peers/node u.pickers/core-node-picker}
   fo/field-styles   {::m.c.peers/node :pick-one}
   fo/id             m.c.peers/id
   fo/route-prefix   "new-peer"
   fo/title          "New Core Peer"})

