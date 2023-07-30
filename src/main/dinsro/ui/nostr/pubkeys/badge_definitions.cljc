(ns dinsro.ui.nostr.pubkeys.badge-definitions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.badge-definitions :as j.n.badge-definitions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :nostr-pubkeys-show-badge-definitions)
(def model-key ::m.n.badge-definitions/id)
(def parent-model-key ::m.n.pubkeys/id)
(def router-key :dinsro.ui.nostr.pubkeys/Router)

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [report-instance _]
             (let [id (u.buttons/get-control-value report-instance ::m.n.pubkeys/id)]
               (comp/transact! report-instance
                 [`(mu.n.pubkeys/fetch-definitions! {::m.n.pubkeys/id ~id})])))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-definitions/code]
   ro/control-layout   {:action-buttons [::fetch ::refresh]}
   ro/controls         {::m.n.pubkeys/id {:type :uuid :label "id"}
                        ::fetch          fetch-button
                        ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk           m.n.badge-definitions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-definitions/index
   ro/title            "Badges Created"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     (fn [props]
                        {parent-model-key (parent-model-key props)
                         ::m.navlinks/id  index-page-key
                         :ui/report       {}})
   :query             (fn []
                        [[::dr/id router-key]
                         parent-model-key
                         ::m.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["badge-definitions"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (ui-report report))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/input-key     parent-model-key
   ::m.navlinks/label         "Badge Definitions"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :nostr-pubkeys-show
   ::m.navlinks/router        :nostr-pubkeys
   ::m.navlinks/required-role :user})
