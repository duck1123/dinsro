(ns dinsro.ui.nostr.subscriptions
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.subscriptions :as j.n.subscriptions]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.subscription-pubkeys :as u.n.subscription-pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/subscriptions.clj][Subscription Actions]]
;; [[../../model/nostr/subscriptions.cljc][Subscriptions Model]]

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.subscriptions/code  #(u.links/ui-subscription-link %3)
                         ::m.n.subscriptions/relay #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.subscriptions/code m.n.subscriptions/relay j.n.subscriptions/pubkey-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "subscriptions"
   ro/row-pk            m.n.subscriptions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.subscriptions/index
   ro/title             "Subscriptions"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.subscription-pubkeys/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.subscriptions/keys [code relay]
          :ui/keys                 [nav-menu router]}]
  {:ident         ::m.n.subscriptions/id
   :initial-state
   (fn [props]
     (let [id (::m.n.subscriptions/id props)]
       {::m.n.subscriptions/id    nil
        ::m.n.subscriptions/code  ""
        ::m.n.subscriptions/relay {}
        :ui/nav-menu              (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :nostr-subscriptions :id id})
        :ui/router                {}}))
   :pre-merge     (u.loader/page-merger
                   ::m.n.subscriptions/id
                   {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :nostr-subscriptions}]
                    :ui/router   [Router {}]})
   :query         [::m.n.subscriptions/id
                   ::m.n.subscriptions/code
                   {::m.n.subscriptions/relay (comp/get-query u.links/RelayLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["subscription" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.subscriptions/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/dl {}
          (dom/dt {} "code")
          (dom/dd {} code)
          (dom/dt {} "relay")
          (dom/dd {} (u.links/ui-relay-link relay)))
        (dom/button {:classes [:.ui :.button]
                     :onClick (fn [this]
                                (log/info :a/b {:e (comp/props this)}))} "click"))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))))
