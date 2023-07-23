(ns dinsro.ui.admin.currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../actions/currencies.clj]]
;; [[../../queries/currencies.clj]]

(def index-page-key :admin-currencies)
(def model-key ::m.currencies/id)
(def show-page-key :admin-currencies-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.currencies/delete!))

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/id           m.currencies/id
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(def new-button
  {:label  "New Currency"
   :type   :button
   :action #(form/create! % NewForm)})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.currencies/name #(u.links/ui-admin-currency-link %3)}
   ro/columns           [m.currencies/name
                         m.currencies/code
                         j.currencies/source-count
                         j.currencies/rate-count]
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.currencies/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.currencies/index
   ro/title             "Currencies"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/id nil}
   :query         [::m.currencies/id]}
  (ui-segment {}
    "TODO: Show Currency"))

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
   :route-segment     ["currencies"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (dom/div :.ui-segment "Failed to load report"))))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target {}})
   :query         (fn [_]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["currency" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if (get props model-key)
    (if target
      (ui-show target)
      (u.debug/load-error props "admin show currency target"))
    (u.debug/load-error props "admin show currency")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Currencies"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Show Currency"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
