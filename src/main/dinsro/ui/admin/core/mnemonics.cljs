(ns dinsro.ui.admin.core.mnemonics
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.mnemonics :as j.c.mnemonics]
   [dinsro.model.core.mnemonics :as m.c.mnemonics]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/mnemonics.cljc]]
;; [[../../../model/core/mnemonics.cljc]]

(def index-page-key :admin-core-mnemonics)
(def model-key ::m.c.mnemonics/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.mnemonics/user #(u.links/ui-user-link %2)}
   ro/columns           [m.c.mnemonics/name
                         m.c.mnemonics/entropy
                         m.c.mnemonics/user]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.c.mnemonics/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.mnemonics/index
   ro/title             "Mnemonics"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["mnemonics"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))
