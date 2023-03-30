(ns dinsro.ui.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.picker-options :as picker-options]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.transactions.debits :as u.t.debits]
   [lambdaisland.glogc :as log]))

(defsc AccountQuery
  [_this _props]
  {:query [::m.accounts/id ::m.accounts/name]
   :ident ::m.accounts/id})

(form/defsc-form NewForm [_this _props]
  {fo/attributes    [m.transactions/description
                     m.transactions/date]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component AccountQuery
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

(form/defsc-form EditForm [_this _props]
  {fo/attributes    [m.transactions/description]
   fo/cancel-route  ["transactions"]
   fo/field-styles  {::m.transactions/account :pick-one}
   fo/field-options {::m.transactions/account
                     {::picker-options/query-key       ::m.accounts/index
                      ::picker-options/query-component AccountQuery
                      ::picker-options/options-xform
                      (fn [_ options]
                        (mapv
                         (fn [{::m.accounts/keys [id name]}]
                           {:text  (str name)
                            :value [::m.accounts/id id]})
                         (sort-by ::m.accounts/name options)))}}
   fo/id            m.transactions/id
   fo/route-prefix  "edit-transaction-form"
   fo/title         "Transaction"})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/date
                        j.transactions/debit-count]
   ro/control-layout   {:action-buttons [::new-transaction ::refresh]}
   ro/controls         {::new-transaction {:label  "New Transaction"
                                           :type   :button
                                           :action (fn [this] (form/create! this NewForm))}
                        ::refresh         u.links/refresh-control}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/route            "transactions"
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.transactions/index
   ro/title            "Transaction Report"})

(defsc Show
  [this {::m.transactions/keys [description date]
         debit-count           ::j.transactions/debit-count
         :ui/keys              [debits]}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/description ""
                   ::m.transactions/id          nil
                   ::m.transactions/date        ""
                   ::j.transactions/debit-count 0
                   :ui/debits                   {}}
   :pre-merge     (u.links/page-merger ::m.transactions/id {:ui/debits u.t.debits/SubPage})
   :query         [::m.transactions/description
                   ::m.transactions/id
                   ::m.transactions/date
                   ::j.transactions/debit-count
                   {:ui/debits (comp/get-query u.t.debits/SubPage)}]
   :route-segment ["transaction" :id]
   :will-enter    (partial u.links/page-loader ::m.transactions/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str description))
     (dom/div {} (str "Debit Count: " debit-count))
     (dom/p {} "Date: " (str date))
     (dom/button {:classes [:.ui :.button]
                  :onClick (fn [_e]
                             (let [props (comp/props this)]
                               (log/info :Show/clicked {:props props})
                               (let [id (::m.transactions/id props)]
                                 (form/edit! this NewForm id))))}
       "Edit"))
   (dom/div  :.ui.segment
     (if debits
       (u.t.debits/ui-sub-page debits)
       (dom/p {} "Transaction debits not loaded")))))
