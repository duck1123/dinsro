(ns dinsro.ui.forms.settings.ln.peers
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.mutations.ln.peers :as mu.ln.peers]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(def debug-props? true)
(def override-form? false)

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _key]
             (let [{::m.ln.peers/keys [address id]
                    node             ::m.ln.peers/node} (comp/props this)
                   {node-id ::m.ln.nodes/id}            node
                   props                               {::m.ln.peers/id   id
                                                        ::m.ln.peers/address address
                                                        ::m.ln.peers/node node-id}]
               (log/info :submit-action/clicked props)
               (comp/transact! this [`(mu.ln.peers/create! ~props)])))})

(form/defsc-form
  NewForm
  [this props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.ln.peers/node
                      m.ln.peers/remote-node]
   fo/controls       {::submit submit-button}
   fo/field-options  {::m.ln.peers/node        u.pickers/ln-node-picker
                      ::m.ln.peers/remote-node u.pickers/remote-node-picker}
   fo/field-styles   {::m.ln.peers/node        :pick-one
                      ::m.ln.peers/remote-node :pick-one}
   fo/id             m.ln.peers/id
   fo/route-prefix   "new-peer"
   fo/title          "New Peer"}
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (dom/div {} "New Peers"))
    (when debug-props? (u.debug/log-props props))))
