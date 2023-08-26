(ns dinsro.ui.ln.channels
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.channels :as j.ln.channels]
   [dinsro.model.ln.channels :as m.ln.channels]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/ln/channels.cljc]]
;; [[../../model/ln/channels.cljc]]

(def index-page-id :ln-channels)
(def model-key ::m.ln.channels/id)
(def required-role :user)
(def show-page-key :ln-channels-show)

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
  {ro/column-formatters {::m.ln.channels/node #(u.links/ui-node-link %2)
                         ::m.ln.channels/id   #(u.links/ui-channel-link %3)}
   ro/columns           [m.ln.channels/id
                         m.ln.channels/channel-point
                         m.ln.channels/node]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.channels/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.channels/index
   ro/title             "Channels"}
  (dom/div {}
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.channels/id
   :initial-state {::m.ln.channels/id nil}
   :query         [::m.ln.channels/id]}
  (dom/div {}
    "TODO: Show channel"))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["channels"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.ln.channels/keys [id]
          ::m.navlinks/keys             [target]
          :as                  props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.ln.channels/id nil
                   ::m.navlinks/id    show-page-key
                   ::m.navlinks/target        {}}
   :query         [::m.ln.channels/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["channel" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))
