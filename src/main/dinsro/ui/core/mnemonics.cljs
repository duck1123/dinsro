(ns dinsro.ui.core.mnemonics
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.mnemonics :as j.c.mnemonics]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.mnemonics/name
                        m.c.mnemonics/entropy
                        m.c.mnemonics/user]
   ro/field-formatters {::m.c.mnemonics/user #(u.links/ui-user-link %2)}
   ro/route            "mnemonics"
   ro/row-pk           m.c.mnemonics/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.mnemonics/index
   ro/title            "Mnemonics"})