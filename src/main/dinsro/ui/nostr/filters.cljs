(ns dinsro.ui.nostr.filters
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.filters.filter-items :as u.n.f.filter-items]))

(def show-page-key :nostr-filters-show)
(def model-key ::m.n.filters/id)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.f.filter-items/SubPage
    u.n.f.filter-items/NewForm]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.filters/keys [id index request]
          :ui/keys           [nav-menu router]}]
  {:ident         ::m.n.filters/id
   :initial-state (fn [props]
                    (let [id (::m.n.filters/id props)]
                      {::m.n.filters/id      nil
                       ::m.n.filters/index   nil
                       ::m.n.filters/request {}
                       :ui/nav-menu          (comp/get-initial-state
                                              u.menus/NavMenu
                                              {::m.navbars/id :nostr-filters :id id})
                       :ui/router            (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger ::m.n.filters/id {:ui/router [Router {}]})
   :query         [::m.n.filters/id
                   ::m.n.filters/index
                   {::m.n.filters/request (comp/get-query u.links/RequestLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["filter" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.filters/id ::Show)}
  (dom/div {:classes [:.ui :.segment]}
    (dom/div {} "filter")
    (dom/div {} (str id))
    (dom/div {} (str index))
    (dom/div {} (u.links/ui-request-link request))
    (u.menus/ui-nav-menu nav-menu)
    (ui-router router)))
