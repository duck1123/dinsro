(ns dinsro.ui.nostr.badge-awards
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.badge-definitions :as j.n.badge-definitions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-key :nostr-badge-awards)
(def model-key ::m.n.badge-awards/id)
(def show-page-key :nostr-badge-awards-show)

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [report-instance {::m.n.pubkeys/keys [id]}]
             (comp/transact! report-instance
               [(mu.n.pubkeys/fetch-awards! {::m.n.pubkeys/id id})]))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-awards/id]
   ro/control-layout   {:action-buttons [::new ::fetch ::refresh]}
   ro/controls         {::fetch   fetch-button
                        ::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.badge-definitions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-definitions/index
   ro/title            "Badge Awards"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.badge-awards/keys [id]
          :as                     props}]
  {:ident         ::m.n.badge-awards/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key id}))
   :query         [::m.n.badge-awards/id]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {} "TODO: Show badge awards0")
    (ui-segment {} "Failed to load record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["badge-awards"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.n.badge-awards/keys [id]
          ::m.navlinks/keys       [target]
          :as                     props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.badge-awards/id nil
                   ::m.navlinks/id       show-page-key
                   ::m.navlinks/target   {}}
   :query         [::m.n.badge-awards/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["badge-award" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {} "Failed to load record")))
