(ns dinsro.ui.admin.ln.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.channels :as j.ln.channels]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/channels.cljc]]
;; [[../../../model/ln/channels.cljc]]

(def index-page-key :admin-ln-channels)
(def model-key ::m.ln.channels/id)

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.ln.channels/id
                    m.ln.channels/active
                    m.ln.channels/capacity
                    m.ln.channels/chan-id
                    m.ln.channels/channel-point
                    m.ln.channels/chan-status-flags
                    m.ln.channels/close-address
                    m.ln.channels/commit-fee]
   fo/id           m.ln.channels/id
   fo/route-prefix "new-channel"
   fo/title        "New Lightning Channels"})

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.channels/id
                        m.ln.channels/channel-point
                        m.ln.channels/node]
   ro/field-formatters {::m.ln.channels/node #(u.links/ui-node-link %2)
                        ::m.ln.channels/id   #(u.links/ui-channel-link %3)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk           m.ln.channels/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.channels/index
   ro/title            "Channels"}
  (dom/div {}
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.channels/id
   :initial-state {::m.ln.channels/id nil}
   :query         [::m.ln.channels/id]
   :route-segment ["channels" :id]}
  (dom/div {}))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["channels"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))
