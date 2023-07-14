(ns dinsro.ui.nostr.runs
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.connections :as u.n.connections]
   [dinsro.ui.nostr.runs.witnesses :as u.n.r.witnesses]))

(def model-key ::m.n.runs/id)
(def show-page-key :nostr-runs-show)

(defsc RunDisplay
  [_this {::m.n.runs/keys [connection] :as props}]
  {:ident         ::m.n.runs/id
   :initial-state {::m.n.runs/id         nil
                   ::m.n.runs/status     {}
                   ::m.n.runs/connection {}}
   :query         [::m.n.runs/id
                   ::m.n.runs/status
                   {::m.n.runs/connection (comp/get-query u.n.connections/ConnectionDisplay)}]}
  (dom/div {} (u.links/ui-run-link props))
  (u.n.connections/ui-connection-display connection))

(def ui-run-display (comp/factory RunDisplay {:keyfn ::m.n.runs/id}))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.r.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.runs/keys [id]
          :ui/keys        [nav-menu router]}]
  {:ident         ::m.n.runs/id
   :initial-state (fn [props]
                    (let [{::m.n.runs/keys [id]} props]
                      {::m.n.runs/id nil
                       :ui/router    (comp/get-initial-state Router)
                       :ui/nav-menu  (comp/get-initial-state
                                       u.menus/NavMenu
                                       {::m.navbars/id :nostr-runs
                                        :id            id})}))
   :pre-merge     (u.loader/page-merger ::m.n.runs/id {:ui/router [Router {}]})
   :query         [::m.n.runs/id
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/div "Run")
        (dom/div {} (str id)))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this {:ui/keys [record]}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id show-page-key
                   :ui/record      {}}
   :query         [::m.navlinks/id
                   {:ui/record (comp/get-query Show)}]
   :route-segment ["run" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (ui-show record))
