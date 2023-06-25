(ns dinsro.ui.admin.nostr.badge-acceptances
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.badge-acceptances :as j.n.badge-acceptances]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/badge_acceptances.cljc]]
;; [[../../../model/nostr/badge_acceptances.cljc]]

(def index-page-key :nostr-badge-acceptances)
(def model-key ::m.n.badge-acceptances/id)
(def show-page-key :nostr-badge-acceptances-show)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-acceptances/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.badge-acceptances/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-acceptances/index
   ro/title            "Acceptances"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.badge-acceptances/keys [id]
          :as                          props}]
  {:ident         ::m.n.badge-acceptances/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key id}))
   :query         [::m.n.badge-acceptances/id]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {} "TODO: Show badge acceptance")
    (ui-segment {} "Failed to load record")))

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
   :route-segment     ["badge-acceptances"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.n.badge-acceptances/keys [id]
          ::m.navlinks/keys      [target]
          :as                    props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.badge-acceptances/id nil
                   ::m.navlinks/id      show-page-key
                   ::m.navlinks/target  {}}
   :query         [::m.n.badge-acceptances/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["badge-acceptance" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {} "Failed to load record")))
