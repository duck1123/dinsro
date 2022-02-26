(ns dinsro.ui.words
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.words :as j.words]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.users :as m.users]
   [dinsro.model.words :as m.words]
   [dinsro.mutations.words :as mu.words]
   [dinsro.ui.links :as u.links]))

(report/defsc-report WordReport
  [_this _props]
  {ro/columns          [m.words/word
                        m.words/position
                        ;; m.words/user
                        ]
   ro/control-layout   {:action-buttons [::new]}
   ;; ro/controls         {::new new-action-button}
   ;; ro/field-formatters {::m.words/node #(u.links/ui-core-node-link %2)
   ;;                      ::m.words/user #(u.links/ui-user-link %2)}
   ;; ro/form-links       {::m.words/name WalletForm}
   ro/route            "words"
   ;; ro/row-actions      [delete-action-button]
   ro/row-pk           m.words/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.words/index
   ro/title            "Word Report"})
