(ns dinsro.ui.forms.admin.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.options.ln.nodes :as o.ln.nodes]
   [dinsro.options.ln.peers :as o.ln.peers]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.ln.peers/keys [address id]
                    node             o.ln.peers/node} (comp/props this)
                   {node-id o.ln.nodes/id}            node
                   props                               {o.ln.peers/id   id
                                                        o.ln.peers/address address
                                                        o.ln.peers/node node-id}]
               (log/info :submit-action/clicked props)
               (comp/transact! this [`(mu.ln.peers/create! ~props)])))})

(form/defsc-form NewForm [_this _props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.ln.peers/node
                      m.ln.peers/remote-node]
   fo/controls       {::submit submit-button}
   fo/field-options  {o.ln.peers/node        u.pickers/admin-ln-node-picker
                      o.ln.peers/remote-node u.pickers/admin-remote-node-picker}
   fo/field-styles   {o.ln.peers/node        :pick-one
                      o.ln.peers/remote-node :pick-one}
   fo/id             m.ln.peers/id
   fo/route-prefix   "new-peer"
   fo/title          "New Peer"})
