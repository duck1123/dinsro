(ns dinsro.ui.currencies.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/accounts.cljc]]
;; [[../../model/accounts.cljc]]

(def index-page-key :currencies-show-accounts)
(def model-key ::m.accounts/id)
(def parent-model-key ::m.currencies/id)
(def router-key :dinsro.ui.currencies/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/currency #(u.links/ui-currency-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/currency
                         j.accounts/transaction-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/index
   ro/title             "Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.currencies/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/debug :SubPage/starting {:props props})
  (if report
    (ui-report report)
    (u.debug/load-error props "currencies accounts page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Accounts"
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :currencies-show
   ::m.navlinks/router        :currencies
   ::m.navlinks/required-role :user})
