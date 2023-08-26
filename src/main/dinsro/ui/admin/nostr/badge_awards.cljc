(ns dinsro.ui.admin.nostr.badge-awards
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.badge-awards :as j.n.badge-awards]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/badge_awards.cljc]]
;; [[../../../model/nostr/badge_awards.cljc]]

(def index-page-id :admin-nostr-badge-awards)
(def model-key ::m.n.badge-awards/id)
(def parent-router-id :admin-nostr)
(def required-role :admin)
(def show-page-key :admin-nostr-badge-awards-show)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-awards/id]
   ro/control-layout   {:action-buttons [::new ::fetch ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.badge-awards/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-awards/index
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
    (ui-segment {} "TODO: Show badge award")
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["badge-awards"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
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
    (u.debug/load-error props "admin show badge awards")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Awards"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/input-key     model-key
   ::m.navlinks/label         "Awards"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
