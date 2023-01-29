(ns dinsro.ui.nostr.subscriptions
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.subscriptions :as j.n.subscriptions]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.subscription-pubkeys :as u.n.subscription-pubkeys]))

;; [[../../actions/nostr/subscriptions.clj][Subscription Actions]]


(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.subscriptions/id
                        m.n.subscriptions/code
                        m.n.subscriptions/relay]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/route            "subscriptions"
   ro/row-pk           m.n.subscriptions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.subscriptions/index
   ro/title            "Subscriptions"})

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.subscription-pubkeys/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "pubkeys"
    :name  "Pubkeys"
    :route "dinsro.ui.nostr.subscription-pubkeys/SubPage"}])

(defsc Show
  [_this {::m.n.subscriptions/keys [id]
          :ui/keys                 [router]
          :as                      props}]
  {:ident         ::m.n.subscriptions/id
   :initial-state {::m.n.subscriptions/id nil
                   :ui/router             {}}
   :pre-merge     (u.links/page-merger ::m.n.subscriptions/id {:ui/router Router})
   :query         [::m.n.subscriptions/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["subscription" :id]
   :will-enter    (partial u.links/page-loader ::m.n.subscriptions/id ::Show)}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (dom/div :.ui.segment
          (dom/dl {}))

        (u.links/ui-nav-menu {:menu-items menu-items :id id})
        (eb/error-boundary
         (if router
           (ui-router router)
           (dom/div :.ui.segment
             (dom/h3 {} "Router not loaded")
             (u.links/ui-props-logger props))))))
    (dom/div :.ui.segment
      (dom/h3 {} "Item not loaded")
      (u.links/ui-props-logger props))))
