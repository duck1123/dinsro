(ns dinsro.ui.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.relay.pubkeys :as u.n.r.pubkeys]
   [dinsro.ui.nostr.relay.requests :as u.n.r.requests]
   [dinsro.ui.nostr.relay.subscriptions :as u.n.r.subscriptions]
   [dinsro.ui.nostr.relay.topics :as u.n.r.topics]
   [dinsro.ui.nostr.requests.filters :as u.n.rq.filters]
   [lambdaisland.glogc :as log]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.rq.filters/SubPage]})

(def menu-items
  [{:key   "filters"
    :name  "Filters"
    :route "dinsro.ui.nostr.requests.filters/SubPage"}])

(defsc Show
  [this {::m.n.requests/keys []
         :ui/keys          [router]
         :as               props}]
  {:route-segment ["request" :id]
   :query         [{:ui/router (comp/get-query Router)}]
   :initial-state {}
   :ident         ::m.n.requests/id
   :pre-merge     (u.links/page-merger ::m.n.requests/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.n.relays/id ::Show)}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (dom/div :.ui.segment
          (dom/div "Request")
          (dom/dl {}
            (dom/dt {} "Address")
            (dom/dd {} (str address))
            (dom/dt {} "Connected")
            (dom/dd {} (str connected))))
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
