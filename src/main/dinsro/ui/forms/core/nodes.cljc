(ns dinsro.ui.forms.core.nodes
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.nodes :as m.c.nodes]))

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

