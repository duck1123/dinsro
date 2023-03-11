(ns dinsro.ui.users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.users :as j.users]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-ln-nodes :as u.user-ln-nodes]
   [dinsro.ui.user-pubkeys :as u.user-pubkeys]
   [dinsro.ui.users.accounts :as u.u.accounts]
   [dinsro.ui.users.debits :as u.u.debits]
   [dinsro.ui.users.transactions :as u.u.transactions]
   [dinsro.ui.users.wallets :as u.u.wallets]))

;; [[../actions/users.clj][User Actions]]
;; [[../joins/users.cljc][User Joins]]
;; [[../model/users.cljc][User Models]]

(def menu-items
  [{:key   "accounts"
    :name  "Accounts"
    :route "dinsro.ui.users.accounts/SubPage"}
   {:key   "debits"
    :name  "Debits"
    :route "dinsro.ui.users.debits/SubPage"}
   {:key   "ln-nodes"
    :name  "LN Nodes"
    :route "dinsro.ui.user-ln-nodes/SubPage"}
   {:key   "pubkeys"
    :name  "Pubkeys"
    :route "dinsro.ui.user-pubkeys/SubPage"}
   {:key   "transactions"
    :name  "Transactions"
    :route "dinsro.ui.users.transactions/SubPage"}
   {:key   "wallets"
    :name  "Wallets"
    :route "dinsro.ui.users.wallets/SubPage"}])

(defrouter Router
  [_this _props]
  {:router-targets
   [u.u.accounts/SubPage
    u.u.debits/SubPage
    u.user-ln-nodes/SubPage
    u.user-pubkeys/SubPage
    u.u.transactions/SubPage
    u.u.wallets/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.users/keys [id name]
          :ui/keys       [router]}]
  {:route-segment ["users" :id]
   :query         [::m.users/name
                   ::m.users/id
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.users/name   ""
                   ::m.users/id     nil
                   :ui/router {}}

   :ident         ::m.users/id
   :pre-merge     (u.links/page-merger
                   ::m.users/id
                   {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.users/id ::Show)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/p {} "Show User " (str name)))
   (u.links/ui-nav-menu {:id id :menu-items menu-items})
   (ui-router router)))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.users/name]
   ro/source-attribute ::j.users/index
   ro/title            "Users"
   ro/route            "users"
   ro/row-pk           m.users/id
   ro/run-on-mount?    true})
