(ns dinsro.ui.nostr.subscription-pubkeys
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.subscription-pubkeys :as j.n.subscription-pubkeys]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.subscription-pubkeys :as m.n.subscription-pubkeys]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.mutations.nostr.subscription-pubkeys :as mu.n.subscription-pubkeys]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/subscriptions.clj]]
;; [[../../ui/nostr/subscriptions.cljs]]

(def ident-key ::m.n.subscriptions/id)
(def index-page-key :nostr-subscription-pubkeys)
(def model-key ::m.n.subscriptions/id)
(def parent-model-key ::m.n.subscriptions/id)
(def router-key :dinsro.ui.nostr.subscriptions/Router)
(def show-page-key :nostr-subscription-pubkeys-show)

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
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Delete" ::m.n.subscription-pubkeys/id mu.n.subscription-pubkeys/delete!)]
   ro/row-pk            m.n.subscription-pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.subscription-pubkeys/index
   ro/title             "Subscription Pubkeys"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this {::m.n.subscription-pubkeys/keys [subscription pubkey]}]
  {:ident         ::m.n.subscription-pubkeys/id
   :initial-state {::m.n.subscription-pubkeys/id           nil
                   ::m.n.subscription-pubkeys/pubkey       {}
                   ::m.n.subscription-pubkeys/subscription {}}
   :query         [::m.n.subscription-pubkeys/id
                   ::m.n.subscription-pubkeys/subscription
                   ::m.n.subscription-pubkeys/pubkey]}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/dl {}
          (dom/dt {} "subscription")
          (dom/dd {} (u.links/ui-subscription-link subscription))
          (dom/dt {} "pubkey")
          (dom/dd {} (u.links/ui-pubkey-link pubkey)))))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id index-page-key])
   :initial-state {::m.navlinks/id index-page-key
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["subscription-pubkeys"]
   :will-enter    (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc SubPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["pubkeys"]}
  (log/debug :SubPage/starting {:props props})
  (ui-report report))

(defsc ShowPage
  [_this {:ui/keys [record]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id show-page-key
                   :ui/record      {}}
   :query         [::m.navlinks/id
                   {:ui/record (comp/get-query Show)}]
   :route-segment ["subscription-pubkey" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (ui-show record))
