(ns dinsro.ui.menus
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   #?(:cljs [com.fulcrologic.rad.routing :as rroute])
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.menus :as mu.menus]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [lambdaisland.glogc :as log]))

;; [[../../../test/dinsro/ui/menus_test.cljc]]

(def always-load? false)
(def log-error-props? false)
(def log-id? false)
(def log-props? false)
(def marker-id ::menu-load)
(def menu-item-id :dinsro.ui.navbars/MenuItem)
(def menu-item-id2 :dinsro.ui.navbars/NavLink)
(def model-key o.navbars/id)

(defn convert-item
  [item]
  (log/trace :convert-item/starting {:item item})
  (let [{::m.navlinks/keys [label id navigate]} item
        control                                 (o.navlinks/control navigate)
        route                                   (if control
                                                  (str (namespace control) "/" (name control))
                                                  "")
        converted-item                          {:name label :key id :route route}]
    (log/debug :convert-item/finished {:item item :converted-item converted-item})
    converted-item))

(defn Menu-did-mount
  [this]
  (let [props        (comp/props this)
        c            (comp/registry-key->class menu-item-id)
        menu-loaded? (mu.menus/menu-loaded? props)
        marker       (get props [df/marker-table marker-id])]
    (if-let [id (model-key props)]
      (if (or always-load? (not menu-loaded?))
        (if-not (df/loading? marker)
          (let [ident [model-key id]]
            (log/debug :Menu-did-mount/loading {:this this :props props :c c})
            (df/load! this ident c
                      {:marker               marker-id
                       :post-mutation        `mu.menus/loaded
                       :post-mutation-params {:id id}}))
          (do
            (log/debug :Menu-did-mount/already-loading {:id id :this this :props props})
            nil))
        (do
          (log/debug :Menu-did-mount/already-loaded {:id id :this this :props props})
          nil))
      (do
        (log/warn :Menu-did-mount/no-key {:props props})
        nil))))

(defn Menu-did-update
  [this]
  (let [props        (comp/props this)
        c            (comp/registry-key->class menu-item-id)
        menu-loaded? (mu.menus/menu-loaded? props)
        marker       (get props [df/marker-table marker-id])]
    (if-let [id (model-key props)]
      (if (or always-load? (not menu-loaded?))
        (if-not (df/loading? marker)
          (let [ident [model-key id]]
            (log/debug :Menu-did-update/loading {:id id :this this :props props :c c :marker marker})
            (df/load! this ident c
                      {:marker               marker-id
                       :post-mutation        `mu.menus/loaded
                       :post-mutation-params {:id id}}))
          (do
            (log/debug :Menu-did-update/already-loading {:id id :this this :props props})
            nil))
        (do
          (log/debug :Menu-did-update/already-loaded {:id id :this this :props props})
          nil))
      (do
        (log/warn :Menu-did-update/no-key {:props props})
        nil))))

(defn on-menu-click
  [this id _e d]
  (log/info :on-menu-click/starting {:d d})
  #?(:clj
     (let [_props [this id d]] nil)
     :cljs
     (if-let [route-name (get (js->clj d) "route")]
       (let [route-kw (keyword route-name)
             route    (comp/registry-key->class route-kw)]
         (log/info :on-menu-click/clicked {:route-kw route-kw :route route :id id})
         (if id
           (do
             (log/debug :NavMenu/click-with-id {:id id})
             (rroute/route-to! this route {:id (str id)}))
           (do
             (log/debug :NavMenu/click-no-id {})
             (rroute/route-to! this route {}))))
       (throw (ex-info "no route" {})))))

(defsc NavMenu
  [this {:keys        [id]
         menu-loaded? mu.menus/menu-loaded?
         children     o.navbars/children
         :or          {children []}
         :as          props}]
  {:componentDidMount  Menu-did-mount
   :componentDidUpdate Menu-did-update
   :ident              ::m.navbars/id
   :initial-state      (fn [props]
                         {model-key             (model-key props)
                          o.navbars/children    []
                          mu.menus/menu-loaded? false
                          :id                   nil})
   :query              (fn []
                         [[df/marker-table marker-id]
                          :id
                          model-key
                          mu.menus/menu-loaded?
                          {o.navbars/children (comp/get-query (comp/registry-key->class menu-item-id2))}])}
  (let [converted-items (map convert-item children)]
    (log/debug :NavMenu/starting
      {:id              id
       :props           props
       :children        children
       :converted-items converted-items})
    (if menu-loaded?
      (if (seq converted-items)
        (dom/div {}
          (when log-id?
            (dom/p {} (str "ID: " id)))
          (ui-menu {:items converted-items :onItemClick (partial on-menu-click this id)})
          (when log-props? (u.debug/log-props props)))
        (u.debug/load-error props "Nav menu children"))
      (dom/div {}
        (dom/div {} (str "Not loaded: " (model-key props)))
        (when log-error-props? (u.debug/log-props props))))))

(def ui-nav-menu
  "Display a nav menu for controlling subpages"
  (comp/factory NavMenu))

(defsc VerticalMenu
  [this {:keys        [id]
         menu-loaded? mu.menus/menu-loaded?
         children     o.navbars/children
         :or          {children []}
         :as          props}]
  {:componentDidMount  Menu-did-mount
   :componentDidUpdate Menu-did-update
   :ident              ::m.navbars/id
   :initial-state      (fn [props]
                         {model-key          (model-key props)
                          o.navbars/children []
                          mu.menus/menu-loaded? false
                          :id                nil})
   :query              (fn []
                         [[df/marker-table marker-id]
                          :id
                          model-key
                          mu.menus/menu-loaded?
                          {o.navbars/children (comp/get-query (comp/registry-key->class menu-item-id2))}])}
  (log/trace :VerticalMenu/starting {:props props})
  (if menu-loaded?
    (let [converted-items (map convert-item children)]
      (ui-menu {:items       converted-items
                :vertical    true
                :onItemClick (partial on-menu-click this id)}))
    (dom/div {}
      (dom/div {} (str "Not loaded: " (model-key props)))
      (when log-error-props? (u.debug/log-props props)))))

(def ui-vertical-menu
  (comp/factory VerticalMenu))
