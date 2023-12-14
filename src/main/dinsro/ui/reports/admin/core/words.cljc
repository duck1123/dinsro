(ns dinsro.ui.reports.admin.core.words
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.options.core.words :as o.c.words]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.c.words/wallet #(u.links/ui-wallet-link %2)}
   ro/columns           [m.c.words/word
                         m.c.words/position
                         m.c.words/mnemonic]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.words/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.words/index
   ro/title             "Word Report"})

(def ui-report (comp/factory Report))
