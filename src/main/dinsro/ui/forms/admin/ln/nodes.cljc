(ns dinsro.ui.forms.admin.ln.nodes
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.ui.pickers :as u.pickers]))

(def override-create-form false)

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.ln.nodes/name
                     m.ln.nodes/host
                     m.ln.nodes/port
                     m.ln.nodes/core-node
                     m.ln.nodes/user]
   fo/cancel-route  ["nodes"]
   fo/field-options {::m.ln.nodes/core-node u.pickers/admin-core-node-picker
                     ::m.ln.nodes/user      u.pickers/admin-user-picker}
   fo/field-styles  {::m.ln.nodes/core-node :pick-one
                     ::m.ln.nodes/user      :pick-one}
   fo/id            m.ln.nodes/id
   fo/route-prefix  "create-node"
   fo/title         "Create Lightning Node"}
  (if override-create-form
    (form/render-layout this props)
    (ui-grid {}
      (ui-grid-row {}
        (ui-grid-column {:width 16}
          (dom/div {}
            (form/render-layout this props)))))))
