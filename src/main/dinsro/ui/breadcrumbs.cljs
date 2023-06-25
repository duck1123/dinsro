(ns dinsro.ui.breadcrumbs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.collections.breadcrumb.ui-breadcrumb :refer [ui-breadcrumb]]
   [com.fulcrologic.semantic-ui.collections.breadcrumb.ui-breadcrumb-divider :refer [ui-breadcrumb-divider]]
   [com.fulcrologic.semantic-ui.collections.breadcrumb.ui-breadcrumb-section :refer [ui-breadcrumb-section]]
   [dinsro.joins.navlinks :as j.navlinks]
   [dinsro.model.navlinks :as m.navlinks]
   [lambdaisland.glogc :as log]))

(defsc PageInfo
  [_this _props]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id    nil
                   ::m.navlinks/label "unset"
                   ::m.navlinks/route nil
                   ::j.navlinks/path  []
                   :ui/router         {}}
   :query         [::m.navlinks/id
                   ::m.navlinks/label
                   ::m.navlinks/route
                   ::j.navlinks/path
                   :ui/router]})

(defsc BreadcrumbLink
  [_this {::m.navlinks/keys [label]
          :as               props}]
  {:ident         ::m.navlinks/id
   :initial-state (fn [x]
                    (log/debug :BreadcrumbLink/initial-state {:x x})
                    (let [{::m.navlinks/keys [id]} x]
                      {::m.navlinks/id    id
                       ::m.navlinks/label (str "Foo-" id)
                       ::m.navlinks/route nil}))
   :query         [::m.navlinks/id
                   ::m.navlinks/label
                   ::m.navlinks/route]}
  (log/trace :BreadcrumbLink/starting {:props props})
  (ui-breadcrumb-section {:link true}
    (str label)))

(def ui-breadcrumb-link
  "Create a breadcrumb link to a navlink"
  (comp/factory BreadcrumbLink {:keyfn ::m.navlinks/id}))

(defsc BreadcrumbDividedLink
  [_this props]
  (comp/fragment
   (ui-breadcrumb-link props)
   (ui-breadcrumb-divider {})))

(def ui-breadcrumb-divided-link
  "Create a breadcrumb link to a navlink"
  (comp/factory BreadcrumbDividedLink {:keyfn ::m.navlinks/id}))

(defsc BreadcrumbsInner
  [_this {::j.navlinks/keys [path]
          :as               props}]
  {:ident         ::m.navlinks/id
   :initial-state {::m.navlinks/id    nil
                   ::m.navlinks/label ""
                   ::j.navlinks/path  []}
   :query         [::m.navlinks/id
                   ::m.navlinks/label
                   {::j.navlinks/path (comp/get-query BreadcrumbLink)}]}
  (log/trace :BreadcrumbsInner/starting {:props props})
  (dom/div {}
    (ui-breadcrumb {}
      (map ui-breadcrumb-divided-link path)
      (ui-breadcrumb-link props))))

(def ui-breadcrumbs-inner (comp/factory BreadcrumbsInner))

(defsc Breadcrumbs
  [_this {:root/keys [current-page]
          :as        props}]
  {:componentDidMount  (fn [this]
                         (let [props (comp/props this)
                               id    (get-in props [:root/current-page ::m.navlinks/id])]
                           (log/debug :Breadcrumbs/did-mount-props
                             {:this  this
                              :props props
                              :id    id})
                           (when id
                             (df/load! this [::m.navlinks/id id] BreadcrumbsInner))))
   :componentDidUpdate (fn [this]
                         (let [props (comp/props this)
                               id    (get-in props [:root/current-page ::m.navlinks/id])]
                           (log/debug :Breadcrumbs/did-update-props
                             {:this  this
                              :props props
                              :id    id})
                           (when id
                             (df/load! this [::m.navlinks/id id] BreadcrumbsInner))))
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
