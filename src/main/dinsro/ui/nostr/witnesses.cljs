(ns dinsro.ui.nostr.witnesses
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.nostr.runs :as u.n.runs]))

;; [[../../model/nostr/witnesses.cljc]]
;; [[../../queries/nostr/witnesses.clj]]

(def index-page-key :nostr-witnesses)

(def log-witness-props false)

(defsc WitnessDisplay
  [_this {::m.n.witnesses/keys [run] :as props}]
  {:ident         ::m.n.witnesses/id
   :query         [::m.n.witnesses/id
                   {::m.n.witnesses/run (comp/get-query u.n.runs/RunDisplay)}]
   :initial-state {::m.n.witnesses/id  nil
                   ::m.n.witnesses/run {}}}
  (ui-list-item {}
    (when log-witness-props (dom/div {} (u.links/ui-witness-link props)))
    (u.n.runs/ui-run-display run)))

(def ui-witness-display (comp/factory WitnessDisplay {:keyfn ::m.n.witnesses/id}))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.witnesses/id
                        m.n.witnesses/event
                        m.n.witnesses/run]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.witnesses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.witnesses/index
   ro/title            "Witnesses"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["witnesses"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))
