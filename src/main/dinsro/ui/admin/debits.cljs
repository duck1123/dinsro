(ns dinsro.ui.admin.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/debits.cljc]]
;; [[../../model/debits.cljc]]

(def index-page-key :admin-debits)
(def model-key ::m.debits/id)
(def show-page-key :admin-debits-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.debits/account     #(u.links/ui-admin-account-link %2)
                         ::m.debits/transaction #(u.links/ui-admin-transaction-link %2)}
   ro/columns           [m.debits/id
                         m.debits/value
                         m.debits/account
                         m.debits/transaction]
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/admin-index
   ro/title             "Admin Debits Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.debits/id
   :initial-state {::m.debits/id nil}
   :query         [::m.debits/id]}
  (ui-segment {}
    "TODO: Show Debit"))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["debits"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["debit" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (ui-segment {} "Failed to load page")))
