(ns dinsro.ui.forms.admin.core.nodes
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.options.core.nodes :as o.c.nodes]
   [dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(form/defsc-form EditForm
  [this props]
  {fo/attributes    [m.c.nodes/name
                     m.c.nodes/host
                     m.c.nodes/port
                     m.c.nodes/network
                     m.c.nodes/rpcuser
                     m.c.nodes/rpcpass]
   fo/field-options {o.c.nodes/network u.pickers/network-picker}
   fo/field-styles  {o.c.nodes/network :pick-one}
   fo/id            m.c.nodes/id
   fo/route-prefix  "edit-node"
   fo/title         "Edit Node"}
  (log/info :EditForm/starting {:props props})
  (form/render-layout this props))

(def ui-edit-form (comp/factory EditForm))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.c.nodes/name
                    m.c.nodes/host
                    m.c.nodes/port
                    m.c.nodes/rpcuser
                    m.c.nodes/rpcpass]
   fo/cancel-route ["nodes"]
   fo/id           m.c.nodes/id
   fo/route-prefix "new-core-node"
   fo/title        "Core Node"})

