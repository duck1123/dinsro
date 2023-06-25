(ns dinsro.ui.admin.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :refer [ui-moment]]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/transactions.cljc]]
;; [[../../model/transactions.cljc]]

(def index-page-key :admin-transactions)
(def model-key ::m.transactions/id)
(def show-page-key :admin-transactions-show)

(form/defsc-form NewForm [_this _props]
  {fo/attributes    [m.transactions/description]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component u.links/AccountLinkForm
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/id            m.transactions/id
   fo/route-prefix  "transaction-form"
   fo/title         "Transaction"})

(def new-button
  {:label  "New Transaction"
   :type   :button
   :action (fn [this] (form/create! this NewForm))})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.transactions/description
                         m.transactions/date
                         j.transactions/debit-count]
   ro/control-layout    {:action-buttons [::new-transaction ::refresh]}
   ro/controls          {::new-transaction new-button
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.transactions/delete!)]
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/admin-index
   ro/title             "Admin Transaction Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [this {::m.transactions/keys [description date id]
         ::j.transactions/keys [debit-count]
         :as                   props}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/description ""
                   ::m.transactions/id          nil
                   ::m.transactions/date        ""
                   ::j.transactions/debit-count 0}
   :query         [::m.transactions/description
                   ::m.transactions/id
                   ::m.transactions/date
                   ::j.transactions/debit-count]}
  (log/debug :Show/starting {:props props})
  (dom/div {}
    (if id
      (ui-segment {}
        (dom/h1 {}
          (str description))
        (dom/div {}
          (str "Debit Count: " debit-count))
        (dom/p {}
          (dom/span {}
            "Date: ")
          (dom/span {}
            (ui-moment {:fromNow true :withTitle true}
              (str date))))
        (dom/button {:classes [:.ui :.button]
                     :onClick (fn [_e]
                                (let [props (comp/props this)]
                                  (log/info :Show/clicked {:props props})
                                  (let [id (::m.transactions/id props)]
                                    (form/edit! this NewForm id))))}
          "Edit"))
      (ui-segment {:color "red" :inverted true} "Failed to load record"))))

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
   :route-segment     ["transactions"]
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
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (ui-segment {:color "red" :inverted true} "Failed to load page")))
