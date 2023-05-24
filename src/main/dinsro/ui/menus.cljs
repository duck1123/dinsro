(ns dinsro.ui.menus
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [lambdaisland.glogc :as log]))

(defn convert-item
  [item]
  (log/trace :convert-item/starting {:item item})
  (let [{::m.navlinks/keys [label id route]} item
        route                                (if route
                                               (str (namespace route) "/" (name route))
                                               "")]
    {:name label :key id :route route}))

(defsc NavMenu
  [this {:keys            [id]
         ::m.navbars/keys [items]
         :or              {items []}
         :as              props}]
  {:componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (if-let [id (::m.navbars/id props)]
                            (let [ident [::m.navbars/id id]
                                  c     (comp/registry-key->class :dinsro.ui.navbars/MenuItem)]
                              (log/trace :NavMenu/loading {:this this :props props :c c})
                              (df/load! this ident c))
                            (do
                              (log/warn :NavMenu/mounted-no-key {:props props})
                              nil))))
   :ident             ::m.navbars/id
   :query             [[df/marker-table '_]
                       :id
                       ::m.navbars/id
                       {::m.navbars/items (comp/get-query (comp/registry-key->class :dinsro.ui.navbars/NavLink))}]
   :initial-state     (fn [props]
                        (log/debug :NavMenu/initial-state {:props props})
                        {::m.navbars/id    (::m.navbars/id props)
                         ::m.navbars/items []
                         :id               nil})}
  (let [converted-items (map convert-item items)]
    (log/trace :NavMenu/starting {:id id :props props :items items :converted-items converted-items})
    (ui-menu {:items converted-items
              :onItemClick
              (fn [_e d]
                (if-let [route-name (get (js->clj d) "route")]
                  (let [route-kw (keyword route-name)
                        route    (comp/registry-key->class route-kw)]
                    (log/info :onItemClick/kw {:route-kw route-kw :route route :id id})
                    (if id
                      (rroute/route-to! this route {:id (str id)})
                      (do
                        (log/info :onItemClick/no-id {})
                        (rroute/route-to! this route {}))))
                  (throw (js/Error. "no route"))))})))

(def ui-nav-menu
  "Display a nav menu for controlling subpages"
  (comp/factory NavMenu))

(defsc VerticalMenu
  [this {:keys            [id]
         ::m.navbars/keys [items]
         :or              {items []}
         :as              props}]
  {:componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (if-let [id (::m.navbars/id props)]
                            (let [ident [::m.navbars/id id]
                                  c     (comp/registry-key->class :dinsro.ui.navbars/MenuItem)]
                              (log/info :VerticalMenu/component-starting {:this this :props props :c c})
                              (df/load! this ident c))
                            (do
                              (log/info :componentDidMount/no-key {})
                              nil))))
   :ident             ::m.navbars/id
   :query             [[df/marker-table '_]
                       :id
                       ::m.navbars/id
                       {::m.navbars/items (comp/get-query (comp/registry-key->class :dinsro.ui.navbars/NavLink))}]
   :initial-state     (fn [props]
                        (log/info :VerticalMenu/initial-state {:props props})
                        {::m.navbars/id    (::m.navbars/id props)
                         ::m.navbars/items []
                         :id               nil})}
  (log/trace :VerticalMenu/starting {:props props})
  (let [converted-items (map convert-item items)]
    (ui-menu
      {:items    converted-items
       :vertical true
       :onItemClick
       (fn [_e d]
         #_(log/info :clicked {:d d})
         (if-let [route-name (get (js->clj d) "route")]
           (let [route-kw (keyword route-name)
                 route    (comp/registry-key->class route-kw)]
             (log/info :onItemClick/kw {:route-kw route-kw :route route :id id})
             (if id
               (rroute/route-to! this route {:id (str id)})
               (do
                 (log/info :onItemClick/no-id {})
                 (rroute/route-to! this route {}))))
           (throw (js/Error. "no route"))))})))

(def ui-vertical-menu
  (comp/factory VerticalMenu))