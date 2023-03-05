(ns dinsro.ui.nostr.pubkeys.badge-definitions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.badge-definitions :as j.n.badge-definitions]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [report-instance _]
             (let [id (u.links/get-control-value report-instance ::m.n.pubkeys/id)]
               (comp/transact! report-instance
                               [(mu.n.pubkeys/fetch-definitions! {::m.n.pubkeys/id id})])))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-definitions/code]
   ro/control-layout   {:action-buttons [::fetch ::refresh]}
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::fetch          fetch-button
                        ::refresh        u.links/refresh-control}
   ro/row-pk           m.n.badge-definitions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-definitions/index
   ro/title            "Badges Created"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["badge-definitions"]}
  ((comp/factory Report) report))
