(ns dinsro.ui.core.words
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.words :as m.c.words]
   ;; [dinsro.model.]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(report/defsc-report Report
  [this props]
  {ro/columns          [m.c.words/word
                        m.c.words/position
                        m.c.words/mnemonic]
   ;; ro/control-layout   {:action-buttons [::new]}
   ro/field-formatters {::m.c.words/wallet #(u.links/ui-wallet-link %2)}
   ro/route            "words"
   ro/row-pk           m.c.words/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.words/index
   ro/title            "Word Report"}
  (log/info :Report/creating {:props props})
  (report/render-layout this))
