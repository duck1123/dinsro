(ns dinsro.ui.nostr.subscription-pubkeys
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.subscription-pubkeys :as j.n.subscription-pubkeys]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.ui.links :as u.links]))

;; [[../../actions/nostr/subscriptions.clj][Subscription Actions]]
;; [[../../ui/nostr/subscriptions.cljs][Subscriptions UI]]

(def ident-key ::m.n.subscriptions/id)
(def router-key :dinsro.ui.nostr.subscriptions/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.subscription-pubkeys/id           #(u.links/ui-subscription-pubkey-link %3)
                         ::m.n.subscription-pubkeys/subscription #(u.links/ui-subscription-link %2)
                         ::m.n.subscription-pubkeys/pubkey       #(u.links/ui-pubkey-link %2)}
   ro/columns           [m.n.subscription-pubkeys/id
                         m.n.subscription-pubkeys/subscription
                         m.n.subscription-pubkeys/pubkey]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "subscription-pubkeys"
   ro/row-pk            m.n.subscription-pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.subscription-pubkeys/index
   ro/title             "Subscription Pubkeys"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["pubkeys"]}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))

(defsc Show
  [_this {::m.n.subscription-pubkeys/keys [id subscription pubkey]
          :as                             props}]
  {:ident         ::m.n.subscription-pubkeys/id
   :initial-state {::m.n.subscription-pubkeys/id           nil
                   ::m.n.subscription-pubkeys/pubkey       {}
                   ::m.n.subscription-pubkeys/subscription {}}
   :query         [::m.n.subscription-pubkeys/id
                   ::m.n.subscription-pubkeys/subscription
                   ::m.n.subscription-pubkeys/pubkey]
   :route-segment ["subscription-pubkey" :id]
   :will-enter    (partial u.links/page-loader ::m.n.subscription-pubkeys/id ::Show)}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (dom/div :.ui.segment
          (dom/dl {}
            (dom/dt {} "subscription")
            (dom/dd {} (u.links/ui-subscription-link subscription))
            (dom/dt {} "pubkey")
            (dom/dd {} (u.links/ui-pubkey-link pubkey))))))
    (dom/div :.ui.segment
      (dom/h3 {} "Item not loaded")
      (u.links/ui-props-logger props))))
