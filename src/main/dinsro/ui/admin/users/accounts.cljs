(ns dinsro.ui.admin.users.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/accounts.cljc]]
;; [[../../../model/accounts.cljc]]

(def ident-key ::m.users/id)
(def index-page-key :admin-users-accounts)
(def model-key ::m.accounts/id)
(def router-key :dinsro.ui.admin.users/Router)

(def override-report true)
(def override-row false)

(declare Report)

(defsc AccountRow
  [this {::m.accounts/keys [name currency] :as props}]
  {:query         [::m.accounts/id ::m.accounts/name
                   {::m.accounts/currency (comp/get-query u.links/CurrencyLinkForm)}]
   :initial-state {::m.accounts/id       nil
                   ::m.accounts/name     ""
                   ::m.accounts/currency {}}}
  (log/info :AccountRow/starting {:props props})
  (if override-row
    (report/render-row this Report props)
    (dom/div :.item.segment
      (dom/div :.header (str name))
      (dom/div :.meta
        (dom/div {}
          "foo"
          (u.links/ui-currency-link currency))))))

(def ui-account-row (comp/factory AccountRow {:keyfn ::m.accounts/id}))

(report/defsc-report Report
  [this props]
  {;; ro/BodyItem          AccountRow
   ro/column-formatters {::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/currency #(u.links/ui-currency-link %2)}
   ro/columns           [m.accounts/name
                         m.accounts/currency]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.users/id {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/index
   ro/title             "Accounts"}
  (let [{::m.accounts/keys [name]
         :ui/keys          [current-rows]} props]
    (if override-report
      (report/render-layout this)
      (dom/div :.ui.segment
        ((report/control-renderer this) this)
        (dom/div {}
          (dom/div {} "Name: " (str name))
          (log/info :Report/info {:props props})
          (dom/div :.ui.items
            (map ui-account-row current-rows)))))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["accounts"]}
  (ui-report report))
