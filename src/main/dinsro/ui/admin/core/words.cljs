(ns dinsro.ui.admin.core.words
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.words :as j.c.words]
   [dinsro.model.core.words :as m.c.words]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/words.cljc]]
;; [[../../../model/core/words.cljc]]

(def index-page-key :admin-core-words)
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
   ro/row-pk            m.c.words/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.words/index
   ro/title             "Word Report"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["words"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))