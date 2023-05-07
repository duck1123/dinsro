(ns dinsro.ui.nostr.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.menus :as me]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.filters.filter-items :as u.n.f.filter-items]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.f.filter-items/SubPage
    u.n.f.filter-items/NewForm]})

(defsc Show
  [_this {::m.n.filters/keys [id index request]
          :ui/keys           [router]}]
  {:ident         ::m.n.filters/id
   :initial-state {::m.n.filters/id      nil
                   ::m.n.filters/index   nil
                   ::m.n.filters/request {}
                   :ui/router            {}}
   :pre-merge     (u.links/page-merger ::m.n.filters/id {:ui/router Router})
   :query         [::m.n.filters/id
                   ::m.n.filters/index
                   {::m.n.filters/request (comp/get-query u.links/RequestLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["filter" :id]
   :will-enter    (partial u.links/page-loader ::m.n.filters/id ::Show)}
  (dom/div {:classes [:.ui :.segment]}
    (dom/div {} "filter")
    (dom/div {} (str id))
    (dom/div {} (str index))
    (dom/div {} (u.links/ui-request-link request))
    (u.links/ui-nav-menu {:menu-items me/nostr-filters-menu-items :id id})
    ((comp/factory Router) router)))
