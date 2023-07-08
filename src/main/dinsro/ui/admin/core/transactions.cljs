(ns dinsro.ui.admin.core.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.transactions :as j.c.transactions]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.transactions :as mu.c.transactions]
   [dinsro.ui.admin.core.transactions.inputs :as u.a.c.t.inputs]
   [dinsro.ui.admin.core.transactions.outputs :as u.a.c.t.outputs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/core/transactions.cljc]]
;; [[../../../model/core/transactions.cljc]]

(def index-page-key :admin-core-transactions)
(def model-key ::m.c.transactions/id)
(def show-page-key :admin-core-transactions-show)

(defsc Show
  "Show a core tx"
  [this {::m.c.transactions/keys [id tx-id hash fetched? block size]
         :ui/keys                [admin-inputs admin-outputs]
         :as                     props}]
  {:ident         ::m.c.transactions/id
   :initial-state {::m.c.transactions/block    {}
                   ::m.c.transactions/fetched? false
                   ::m.c.transactions/hash     ""
                   ::m.c.transactions/id       nil
                   ::m.c.transactions/size     0
                   ::m.c.transactions/tx-id    nil
                   :ui/admin-inputs            {}
                   :ui/admin-outputs           {}}
   :query         [[df/marker-table '_]
                   {::m.c.transactions/block (comp/get-query u.links/BlockHeightLinkForm)}
                   ::m.c.transactions/fetched?
                   ::m.c.transactions/hash
                   ::m.c.transactions/id
                   ::m.c.transactions/size
                   ::m.c.transactions/tx-id
                   {:ui/admin-inputs (comp/get-query u.a.c.t.inputs/SubPage)}
                   {:ui/admin-outputs (comp/get-query u.a.c.t.outputs/SubPage)}]}
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} "Transaction")
        (dom/dl {}
          (dom/dt {} "TX id")
          (dom/dd {} (str tx-id))
          (dom/dt {} "Hash: ")
          (dom/dd {} (str hash))
          (dom/dt {} "Block: ")
          (dom/dd {} (u.links/ui-block-height-link block))
          (dom/dt {} "Fetched")
          (dom/dd {} (dom/a {:onClick #(comp/transact! this [(mu.c.transactions/fetch! {model-key id})])
                             :href    "#"}
                       (str fetched?)))
          (dom/dt {} "Size")
          (dom/dd {} (str size))))
      (dom/div {}
        (if admin-inputs
          (ui-segment {}
            (u.a.c.t.inputs/ui-subpage admin-inputs))
          (u.debug/load-error props "Admin show core transaction inputs"))
        (if admin-outputs
          (ui-segment {}
            (u.a.c.t.outputs/ui-subpage admin-outputs))
          (u.debug/load-error props "Admin show core transaction outputs"))))
    (u.debug/load-error props "Admin show core transaction record")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.transactions/block #(u.links/ui-admin-block-height-link %2)
                         ::m.c.transactions/tx-id #(u.links/ui-admin-core-tx-link %3)}
   ro/columns           [m.c.transactions/tx-id
                         m.c.transactions/fetched?
                         m.c.transactions/block]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Fetch" model-key mu.c.transactions/fetch!)
                         (u.buttons/row-action-button "Delete" model-key mu.c.transactions/delete!)]
   ro/row-pk            m.c.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.transactions/index
   ro/title             "Transactions"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id show-page-key
                   ::m.navlinks/target      {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (ui-show target))
