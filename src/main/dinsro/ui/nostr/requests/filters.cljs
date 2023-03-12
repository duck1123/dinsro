(ns dinsro.ui.nostr.requests.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.requests/id)
(def router-key :dinsro.ui.nostr.requests/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.filters/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::m.n.requests/id {:type :uuid :label "id"}
                        ::refresh         u.links/refresh-control}
   ro/field-formatters {::m.n.pubkeys/name #(u.links/ui-pubkey-name-link %3)
                        ::m.n.pubkeys/picture
                        (fn [_ picture] (if picture
                                          (dom/img {:src picture :width 100 :height 100})
                                          ""))}
   ro/row-actions      []
   ro/row-pk           m.n.filters/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.filters/index
   ro/title            "Filters"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["filters"]}
  ((comp/factory Report) report))
