(ns dinsro.ui.nostr.pubkeys
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.pubkey-relays :as u.n.pubkey-relays]
   [dinsro.ui.nostr.pubkey-users :as u.n.pubkey-users]
   [lambdaisland.glogc :as log]))

(defrouter Router
  [_this _props]
  {:router-targets [u.n.pubkey-users/SubPage
                    u.n.pubkey-relays/SubPage]})

(def ui-router (comp/factory Router))

(def menu-items
  [{:key   "users"
    :name  "Users"
    :route "dinsro.ui.nostr.pubkey-users/SubPage"}
   {:key   "relays"
    :name  "Relays"
    :route "dinsro.ui.nostr.pubkey-relays/SubPage"}])

(defsc Show
  "Show a core node"
  [this {::m.n.pubkeys/keys [id pubkey name picture]
         :ui/keys           [router]
         :as                props}]
  {:route-segment ["pubkey" :id]
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/pubkey
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/picture
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/pubkey  ""
                   ::m.n.pubkeys/name    ""
                   ::m.n.pubkeys/picture ""
                   :ui/router            {}}
   :ident         ::m.n.pubkeys/id
   :pre-merge     (u.links/page-merger ::m.n.pubkeys/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.n.pubkeys/id ::Show)}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (dom/div :.ui.segment
          (dom/dl {}
            (dom/dt {} "Pubkey")
            (dom/dd {} (str pubkey))
            (dom/dt {} "Name")
            (dom/dd {} (str name))
            (dom/dt {} "Picture")
            (dom/dd {} (str picture)))
          (dom/button {:classes [:.ui.button]
                       :onClick (fn [_e]
                                  (log/info :click {})
                                  (comp/transact! this [(mu.n.pubkeys/fetch! {::m.n.pubkeys/id id})]))}

            "Fetch"))
        (u.links/ui-nav-menu {:menu-items menu-items :id id})
        (if router
          (ui-router router)
          (dom/div :.ui.segment
            (dom/h3 {} "Network Router not loaded")
            (u.links/ui-props-logger props)))))
    (dom/div :.ui.segment
      (dom/h3 {} "Node not loaded")
      (u.links/ui-props-logger props))))

(form/defsc-form CreateForm
  [_this _props]
  {fo/id            m.n.pubkeys/id
   fo/attributes    [m.n.pubkeys/pubkey]
   fo/cancel-route  ["pubkeys"]
   fo/route-prefix  "create-pubkey"
   fo/title         "Create A Pubkey"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this CreateForm))})

(defn fetch-action
  [report-instance {::m.n.pubkeys/keys [id]}]
  (comp/transact! report-instance [(mu.n.pubkeys/fetch! {::m.n.pubkeys/id id})]))

(def fetch-action-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/pubkey]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.pubkeys/pubkey #(u.links/ui-pubkey-link %3)}
   ro/route            "nodes"
   ro/row-actions      [fetch-action-button]
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.n.pubkeys/index
   ro/title            "Pubkey Report"})
