(ns dinsro.ui.nostr.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.filters.filter-items :as u.n.f.filter-items]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.f.filter-items/SubPage]})

(def menu-items
  [{:key "items" :name "Items" :route "dinsro.ui.nostr.filters.filter-items/SubPage"}])

(defsc Show
  [_this {::m.n.filters/keys [id index request]}]
  {:ident         ::m.n.filters/id
   :initial-state {::m.n.filters/id      nil
                   ::m.n.filters/index   nil
                   ::m.n.filters/request {}}
   :query         [::m.n.filters/id
                   ::m.n.filters/index
                   {::m.n.filters/request (comp/get-query u.links/RequestLinkForm)}]
   :route-segment ["filter" :id]
   :will-enter    (partial u.links/page-loader ::m.n.filters/id ::Show)}
  (dom/div {:classes [:.ui :.segment]}
    (dom/div {} "filter")
    (dom/div {} (str id))
    (dom/div {} (str index))
    (dom/div {} (u.links/ui-request-link request))))
