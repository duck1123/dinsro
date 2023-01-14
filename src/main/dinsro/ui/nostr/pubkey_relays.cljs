(ns dinsro.ui.nostr.pubkey-relays
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.tx :as j.c.tx]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.tx/tx-id
                        j.c.tx/node
                        m.c.tx/fetched?
                        m.c.tx/block]
   ro/controls         {::m.c.nodes/id {:type :uuid :label "id"}
                        ::refresh      u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.tx/block #(u.links/ui-block-height-link %2)
                        ::m.c.tx/node  #(u.links/ui-core-node-link %2)
                        ::m.c.tx-id    #(u.links/ui-core-tx-link %3)}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Relays"
   ro/row-actions      [u.c.tx/fetch-action-button u.c.tx/delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query         [::m.c.nodes/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["relays"]
   :initial-state {::m.c.nodes/id nil
                   :ui/report     {}}
   :ident         (fn [] [:component/id ::SubPage])}
  (dom/div :.ui.segment
    (ui-report report)))

(def ui-sub-page (comp/factory SubPage))
