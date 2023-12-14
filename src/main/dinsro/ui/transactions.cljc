(ns dinsro.ui.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navlinks :as m.navlinks :refer [defroute]]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.transactions :as o.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.forms.transactions :as u.f.transactions]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.reports.transactions :as u.r.transactions]
   [dinsro.ui.transactions.debits :as u.t.debits]
   [lambdaisland.glogc :as log]))

;; [[../joins/transactions.cljc]]
;; [[../model/transactions.cljc]]
;; [[../ui/admin/transactions.cljs]]
;; [[../ui/forms/transactions.cljc]]
;; [[../../../test/dinsro/ui/transactions_test.cljs]]

(def index-page-id :transactions)
(def model-key o.transactions/id)
(def parent-router-id :root)
(def required-role :user)
(def show-page-id :transactions-show)

(defsc Show
  [this {::m.transactions/keys [description date id]
         :ui/keys              [debits]
         :as                   props}]
  {:ident         ::m.transactions/id
   :initial-state (fn [props]
                    {o.transactions/description ""
                     o.transactions/id          (model-key props)
                     o.transactions/date        ""
                     :ui/debits                 (comp/get-initial-state u.t.debits/SubSection {})})
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/debits [u.t.debits/SubSection {}]})
   :query         (fn []
                    [o.transactions/description
                     o.transactions/id
                     o.transactions/date
                     {:ui/debits (comp/get-query u.t.debits/SubSection)}])}
  (log/debug :Show/starting {:props props})
  (if id
    (dom/div {}
      (ui-segment {}
        (dom/h1 {} (str description))
        (dom/p {}
          (dom/span {} "Date: ")
          (dom/span {} (u.controls/relative-date date)))
        (u.buttons/form-edit-button this model-key "Edit" u.f.transactions/NewTransaction))
      (if debits
        (ui-segment {}
          (u.t.debits/ui-sub-section debits))
        (u.debug/load-error props "show transaction debits")))
    (u.debug/load-error props "show transaction record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [form report]
          :as      props}]
  {:componentDidMount #(report/start-report! % u.r.transactions/Report {})
   :ident             (fn [_] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/form      (comp/get-initial-state u.f.transactions/CreateTransactionForm {})
                         :ui/report    (comp/get-initial-state u.r.transactions/Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/form (comp/get-query u.f.transactions/CreateTransactionForm)}
                         {:ui/report (comp/get-query u.r.transactions/Report)}])
   :route-segment     ["transactions"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/finest :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (dom/div {}
        (u.f.transactions/ui-create-transaction-form form)
        (u.r.transactions/ui-report report))
      (u.debug/load-error props "index transactions"))))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["transaction" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Transactions"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Transaction"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
