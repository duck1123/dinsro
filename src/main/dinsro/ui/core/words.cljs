(ns dinsro.ui.core.words
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/core/words.cljc]]
;; [[../../model/core/words.cljc]]

(def model-key ::m.c.words/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.words/wallet #(u.links/ui-wallet-link %2)}
   ro/columns           [m.c.words/word
                         m.c.words/position
                         m.c.words/mnemonic]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "words"
   ro/row-pk            m.c.words/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.words/index
   ro/title             "Word Report"})
