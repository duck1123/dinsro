(ns dinsro.ui.nostr.requests.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.mutations.nostr.filters :as mu.n.filters]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.requests/id)
(def router-key :dinsro.ui.nostr.requests/Router)

(defn sub-page-action-button
  [options]
  (let [{:keys [label mutation parent-key]} options]
    {:type :button
     :label label
     :action
     (fn [report-instance]
       (let [parent-id (u.links/get-control-value report-instance parent-key)
             props {parent-key parent-id}]
         (comp/transact! report-instance [(mutation props)])))}))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.filters/index]
   ro/controls         {::m.n.requests/id {:type :uuid :label "id"}
                        ::add-filter      (sub-page-action-button
                                           {:label      "Add Filter"
                                            :mutation   mu.n.filters/add-filter!
                                            :parent-key ident-key})
                        ::refresh         u.links/refresh-control}
   ro/control-layout   {:action-buttons [::add-filter ::new ::refresh]}
   ro/field-formatters {::m.n.filters/index #(u.links/ui-filter-link %3)}
   ro/source-attribute ::j.n.filters/index
   ro/title            "Filters"
   ro/row-pk           m.n.filters/id
   ro/run-on-mount?    true})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["filters"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
