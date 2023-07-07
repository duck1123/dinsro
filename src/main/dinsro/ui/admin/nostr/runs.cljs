(ns dinsro.ui.admin.nostr.runs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.mutations.nostr.runs :as mu.n.runs]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/runs.cljc]]
;; [[../../../model/nostr/runs.cljc]]

(def index-page-key :admin-nostr-runs)
(def model-key ::m.n.runs/id)
(def show-page-key :admin-nostr-runs-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/connection #(u.links/ui-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-request-link %2)
                         ::j.n.runs/relay      #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.runs/status
                         m.n.runs/request
                         m.n.runs/connection
                         m.n.runs/start-time
                         m.n.runs/finish-time
                         m.n.runs/end-time
                         j.n.runs/relay]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "runs"
   ro/row-actions       [(u.buttons/row-action-button "Stop" model-key mu.n.runs/stop!)]
   ro/row-pk            m.n.runs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.runs/index
   ro/title             "Runs"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.runs/keys [id]
          :as             props}]
  {:ident         ::m.n.runs/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key id}))
   :query         [::m.n.runs/id]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {} "TODO: Show run")
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["runs"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.n.runs/keys   [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.runs/id       nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.n.runs/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["event" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))
