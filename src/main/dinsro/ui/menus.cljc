(ns dinsro.ui.menus
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.debug :as u.debug]
   [lambdaisland.glogc :as log]))

(def log-id? false)
(def log-props? false)

(defn convert-item
  [item]
  (log/trace :convert-item/starting {:item item})
  (let [{::m.navlinks/keys [label id navigate]} item
        control                                 (::m.navlinks/control navigate)
        route                                   (if control
                                                  (str (namespace control) "/" (name control))
                                                  "")
        converted-item                          {:name label :key id :route route}]
    (log/debug :convert-item/finished {:item item :converted-item converted-item})
    converted-item))

(defsc NavMenu
  [this {:keys            [id]
         ::m.navbars/keys [children]
         :or              {children []}
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
                       {::m.navbars/children (comp/get-query (comp/registry-key->class :dinsro.ui.navbars/NavLink))}]
   :initial-state     (fn [props]
                        (log/debug :NavMenu/initial-state {:props props})
                        {::m.navbars/id       (::m.navbars/id props)
                         ::m.navbars/children []
                         :id                  nil})}
  (let [converted-items (map convert-item children)]
    (log/debug :NavMenu/starting
      {:id              id
       :props           props
       :children        children
       :converted-items converted-items})
    (if (seq converted-items)
      (dom/div {}
        (when log-id?
          (dom/p {} (str "ID: " id)))
        (ui-menu
          {:items       converted-items
           :onItemClick (fn [_e d]
                          (log/info :NavMenu/onItemClick {:d d})
                          (if-let [route-name #?(:clj nil :cljs (get (js->clj d) "route"))]
                            (let [route-kw (keyword route-name)
                                  route    (comp/registry-key->class route-kw)]
                              (log/info :NavMenu/clicked {:route-kw route-kw :route route :id id})
                              (if id
                                (do
                                  (log/debug :NavMenu/click-with-id {:id id})
                                  (rroute/route-to! this route {:id (str id)}))
                                (do
                                  (log/debug :NavMenu/click-no-id {})
                                  (rroute/route-to! this route {}))))
                            (throw (ex-info "no route" {}))))})
        (when log-props? (u.debug/log-props props)))
      (u.debug/load-error props "Nav menu children"))))

(def ui-nav-menu
  "Display a nav menu for controlling subpages"
  (comp/factory NavMenu))

(defsc VerticalMenu
  [this {:keys            [id]
         ::m.navbars/keys [children]
         :or              {children []}
         :as              props}]
  {:componentDidMount (fn [this]
                        (let [props (comp/props this)]
                          (if-let [id (::m.navbars/id props)]
                            (let [ident [::m.navbars/id id]
                                  c     (comp/registry-key->class :dinsro.ui.navbars/MenuItem)]
                              (log/info :VerticalMenu/component-starting {:this this :props props :c c})
                              (df/load! this ident c))
                            (do
                              (log/info :VerticalMenu/did-mount-no-key {:props props})
                              nil))))
   :ident             ::m.navbars/id
   :query             [[df/marker-table '_]
                       :id
                       ::m.navbars/id
                       {::m.navbars/children (comp/get-query (comp/registry-key->class :dinsro.ui.navbars/NavLink))}]
   :initial-state     (fn [props]
                        (log/trace :VerticalMenu/initial-state {:props props})
                        {::m.navbars/id       (::m.navbars/id props)
                         ::m.navbars/children []
                         :id                  nil})}
  (log/trace :VerticalMenu/starting {:props props})
  (let [converted-items (map convert-item children)]
    (ui-menu
      {:items    converted-items
       :vertical true
       :onItemClick
       (fn [_e d]
         #_(log/info :clicked {:d d})
         (if-let [route-name (get
                              #?(:clj (do
                                        (comment d)

                                        {})
                                 :cljs (js->clj d)) "route")]
           (let [route-kw (keyword route-name)
                 route    (comp/registry-key->class route-kw)]
             (log/info :onItemClick/kw {:route-kw route-kw :route route :id id})
             (if id
               (rroute/route-to! this route {:id (str id)})
               (do
                 (log/info :onItemClick/no-id {})
                 (rroute/route-to! this route {}))))
           (throw (ex-info "no route" {}))))})))

(def ui-vertical-menu
  (comp/factory VerticalMenu))
