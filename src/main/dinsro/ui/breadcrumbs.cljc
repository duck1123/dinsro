(ns dinsro.ui.breadcrumbs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.collections.breadcrumb.ui-breadcrumb :refer [ui-breadcrumb]]
   [com.fulcrologic.semantic-ui.collections.breadcrumb.ui-breadcrumb-divider :refer [ui-breadcrumb-divider]]
   [com.fulcrologic.semantic-ui.collections.breadcrumb.ui-breadcrumb-section :refer [ui-breadcrumb-section]]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [lambdaisland.glogc :as log]))

;; [[../../../test/dinsro/ui/breadcrumbs_test.cljs]]

(def log-props? false)

;; The target information for a breadcrumb
;; This pulls the information about the control
(defsc NavTarget
  [_this _props]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id      nil
                   ::m.navlinks/control nil}
   :query         [::m.navlinks/id ::m.navlinks/control]})

;; A single link in the breadcrumbs display
(defsc BreadcrumbLink
  [this {::m.navlinks/keys [label]
         :as               props}]
  {:ident         ::m.navlinks/id
   :initial-state (fn [props]
                    (log/debug :BreadcrumbLink/initial-state {:props props})
                    (let [{::m.navlinks/keys [id]} props]
                      {::m.navlinks/id     id
                       o.navlinks/label  ""
                       o.navlinks/navigate (comp/get-initial-state NavTarget {})}))
   :query         (fn []
                    [o.navlinks/id
                     o.navlinks/label
                     {o.navlinks/navigate (comp/get-query NavTarget)}])}
  (log/trace :BreadcrumbLink/starting {:props props})
  (ui-breadcrumb-section
    {:link    true
     :onClick (fn [evt]
                (log/info :BreadcrumbLink/clicked {:evt evt :props props})
                (let [data [`(dinsro.mutations.navbars/navigate! ~props)]]
                  (log/info :BreadcrumbLink/clicked {:data data})
                  (comp/transact! this data)))}

    (str label)))

(def ui-breadcrumb-link
  "Create a breadcrumb link to a navlink"
  (comp/factory BreadcrumbLink {:keyfn ::m.navlinks/id}))

;; A link followed by a divider
(defsc BreadcrumbDividedLink
  [_this props]
  (comp/fragment
   (ui-breadcrumb-link props)
   (ui-breadcrumb-divider {})))

(def ui-breadcrumb-divided-link
  "Create a breadcrumb link to a navlink"
  (comp/factory BreadcrumbDividedLink {:keyfn ::m.navlinks/id}))

;; A breadcrumbs control (minus loading behavior)
(defsc BreadcrumbsInner
  [_this {::j.navlinks/keys [path]
          :as               props}]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id       nil
                   ::m.navlinks/label    ""
                   ::m.navlinks/navigate {}
                   ::j.navlinks/path     []}
   :query         [::m.navlinks/id
                   ::m.navlinks/label
                   {::m.navlinks/navigate (comp/get-query NavTarget)}
                   {::j.navlinks/path (comp/get-query BreadcrumbLink)}]}
  (log/trace :BreadcrumbsInner/starting {:props props})
  (dom/div {}
    (ui-breadcrumb {}
      (map ui-breadcrumb-divided-link path)
      (ui-breadcrumb-link props))
    (when log-props?
      (u.debug/log-props props))))

(def ui-breadcrumbs-inner (comp/factory BreadcrumbsInner))

(defn Breadcrumbs-did-mount
  [this]
  (let [props (comp/props this)
        id    (get-in props [:root/current-page ::m.navlinks/id])]
    (log/info :Breadcrumbs-did-mount/starting
      {:this  this
       :props props
       :id    id})
    (when (and id (not (::j.navlinks/path (:root/current-page props))))
      (df/load! this [::m.navlinks/id id] BreadcrumbsInner))))

(defn Breadcrumbs-did-update
  [this]
  (let [props (comp/props this)
        id    (get-in props [:root/current-page ::m.navlinks/id])]
    (log/trace :Breadcrumbs-did-update/starting
      {:this  this
       :props props
       :id    id})
    (when id
      (df/load! this [::m.navlinks/id id] BreadcrumbsInner))))

(defsc Breadcrumbs
  [_this {:root/keys [current-page]
          :as        props}]
  {:componentDidMount  Breadcrumbs-did-mount
   :componentDidUpdate Breadcrumbs-did-update
   :ident              (fn [] [:component/id ::Breadcrumbs])
   :initial-state      {:root/current-page {}}
   :pre-merge          (fn [{:keys [data-tree] :as ctx}]
                         (log/debug :Breadcrumbs/merging {:ctx ctx})
                         (assoc-in data-tree [:ui/home-link ::m.navlinks/id] :home))
   :query              [{[:root/current-page '_] (comp/get-query BreadcrumbsInner)}]}
  (log/debug :Breadcrumbs/starting {:props props :current-page current-page})
  (let [{::m.navlinks/keys [id]} current-page]
    (dom/div {}
      (if id
        (ui-breadcrumbs-inner current-page)
        (dom/div {} "Failed to load breadcrumbs")))))

(def ui-breadcrumbs (comp/factory Breadcrumbs))
