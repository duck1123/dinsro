(ns dinsro.ui.admin.users.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.users :as m.users]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/accounts.cljc]]
;; [[../../../model/accounts.cljc]]

(def index-page-id :admin-users-show-accounts)
(def model-key ::m.accounts/id)
(def parent-model-key ::m.users/id)
(def parent-router-id :admin-users-show)
(def required-role :admin)
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
    (dom/div :.item
      (ui-segment {}
        (dom/div :.header (str name))
        (dom/div :.meta
          (dom/div {}
            (u.links/ui-currency-link currency)))))))

(def ui-account-row (comp/factory AccountRow {:keyfn model-key}))

(report/defsc-report Report
  [this props]
  {;; ro/BodyItem          AccountRow
   ro/column-formatters {::m.accounts/name     #(u.links/ui-account-link %3)
                         ::m.accounts/currency #(u.links/ui-currency-link %2)
                         ::m.accounts/user     #(when %2 (u.links/ui-admin-user-link %2))}
   ro/columns           [m.accounts/name
                         m.accounts/currency
                         m.accounts/user]
   ro/control-layout    {:action-buttons [::refresh]
                         :inputs         [[parent-model-key]]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.accounts/admin-index
   ro/title             "Accounts"}
  (let [{::m.accounts/keys [name]
         :ui/keys          [current-rows]} props]
    (if override-report
      (report/render-layout this)
      (ui-segment {}
        ((report/control-renderer this) this)
        (dom/div {}
          (dom/div {} "Name: " (str name))
          (log/info :Report/info {:props props})
          (dom/div :.ui.items
            (map ui-account-row current-rows)))))))

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {::m.navlinks/id  index-page-id
                         parent-model-key (parent-model-key props)
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["accounts"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Accounts"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
