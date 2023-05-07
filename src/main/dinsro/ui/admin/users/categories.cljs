(ns dinsro.ui.admin.users.categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

;; [../../../model/categories.cljc]
;; [../../../ui/categories.cljs]

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(def override-report false)
(def override-row false)
(def show-controls true)

(declare Report)

(defsc BodyItem
  [this {::j.categories/keys [transaction-count]
         :as                 props}]
  {:ident         ::m.categories/id
   :query         [::m.categories/id
                   ::m.categories/name
                   ::j.categories/transaction-count]
   :initial-state {::m.categories/id                nil
                   ::m.categories/name              ""
                   ::j.categories/transaction-count 0}}
  (if override-row
    (report/render-row this Report props)
    (dom/div :.ui.item.segment
      (dom/div :.header
        (u.links/ui-category-link props))
      (dom/div :.meta
        (str "Transactions: " transaction-count)))))

(def ui-body-item (comp/factory BodyItem {:keyfn ::m.categories/id}))

(report/defsc-report Report
  [this props]
  {ro/BodyItem          BodyItem
   ro/columns           [m.categories/id]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.users/id {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/row-pk            m.categories/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.categories/index
   ro/title             "Categories"}
  (let [{:ui/keys          [current-rows]} props]
    (if override-report
      (report/render-layout this)
      (dom/div :.ui.items.segment
        (when show-controls ((report/control-renderer this) this))
        (dom/div {}
          (log/info :Report/info {:props props})
          (dom/div {}
            (map ui-body-item current-rows)))))))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["categories"]}
  ((comp/factory Report) report))
