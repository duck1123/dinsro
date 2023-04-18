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
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.pubkeys.badge-acceptances :as u.n.p.badge-acceptances]
   [dinsro.ui.nostr.pubkeys.badge-awards :as u.n.p.badge-awards]
   [dinsro.ui.nostr.pubkeys.badge-definitions :as u.n.p.badge-definitions]
   [dinsro.ui.nostr.pubkeys.contacts :as u.n.p.contacts]
   [dinsro.ui.nostr.pubkeys.events :as u.n.p.events]
   [dinsro.ui.nostr.pubkeys.items :as u.n.p.items]
   [dinsro.ui.nostr.pubkeys.relays :as u.n.p.relays]
   [dinsro.ui.nostr.pubkeys.users :as u.n.p.users]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../mutations/nostr/pubkey_contacts.cljc][Pubkey Contact Mutations]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkey Mutations]]

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.p.badge-acceptances/SubPage
    u.n.p.badge-awards/SubPage
    u.n.p.badge-definitions/SubPage
    u.n.p.items/SubPage
    u.n.p.relays/SubPage
    u.n.p.contacts/SubPage
    u.n.p.events/SubPage
    u.n.p.users/SubPage]})

(defn img-formatter
  [pubkey]
  (if-let [picture (::m.n.pubkeys/picture pubkey)]
    (dom/img {:src picture :height 100 :width 100})
    ""))

(def menu-items
  [{:key "events"          :name "Events"          :route "dinsro.ui.nostr.pubkeys.events/SubPage"}
   {:key "badges-created"  :name "Badges Created"  :route "dinsro.ui.nostr.pubkeys.badge-definitions/SubPage"}
   {:key "badges-awarded"  :name "Badges Awarded"  :route "dinsro.ui.nostr.pubkeys.badge-awards/SubPage"}
   {:key "badges-accepted" :name "Badges Accepted" :route "dinsro.ui.nostr.pubkeys.badge-acceptances/SubPage"}
   {:key "items"           :name "Filter Items"           :route "dinsro.ui.nostr.pubkeys.items/SubPage"}
   {:key "relays"          :name "Relays"          :route "dinsro.ui.nostr.pubkeys.relays/SubPage"}])

(def show-border false)

(defsc PubkeyInfo
  [this {::j.n.pubkeys/keys [npub]
         ::m.n.pubkeys/keys [about display-name hex id lud06 name nip05 picture website]}]
  {:css           [[:.content-box (merge {:overflow "hidden"} (when show-border {:border "1px solid green !important"}))]
                   [:.info (merge {} (when show-border {:border "1px solid red"}))]
                   [:.picture-container (merge {} (when show-border {:border "1px solid purple"}))]]
   :ident         ::m.n.pubkeys/id
   :initial-state {::j.n.pubkeys/npub        ""
                   ::m.n.pubkeys/about        ""
                   ::m.n.pubkeys/display-name ""
                   ::m.n.pubkeys/hex          ""
                   ::m.n.pubkeys/id           nil
                   ::m.n.pubkeys/lud06        ""
                   ::m.n.pubkeys/name         ""
                   ::m.n.pubkeys/nip05        ""
                   ::m.n.pubkeys/picture      ""
                   ::m.n.pubkeys/website      ""}
   :query         [::j.n.pubkeys/npub
                   ::m.n.pubkeys/about
                   ::m.n.pubkeys/display-name
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/id
                   ::m.n.pubkeys/lud06
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/nip05
                   ::m.n.pubkeys/picture
                   ::m.n.pubkeys/website]}
  (let [avatar-size                                       200
        {:keys [content-box info picture-container]} (css/get-classnames PubkeyInfo)]
    (dom/div :.ui.segment
      (dom/div :.ui.items.unstackable
        (dom/div {:classes [:.item info]}
          (dom/div {:classes [:.ui :.tiny :.image picture-container]}
            (when picture
              (dom/img {:src (str picture) :width avatar-size :height avatar-size})))
          (dom/div {:classes [:.content content-box]}
            (dom/div :.header (str (or display-name name)))
            (dom/div :.meta (str nip05))
            (dom/div :.ui.description
              (dom/div {} (str (or npub hex)))
              (dom/div {} (str about))
              (dom/div {} (str website))
              (dom/div {} (str lud06)))
            (dom/div :.extra
              (dom/button
                {:classes [:.ui.right.floated.button.secondary]
                 :onClick (fn [_e] (comp/transact! this [(mu.n.pubkeys/fetch! {::m.n.pubkeys/id id})]))}
                "Fetch Info"))))))))

(def ui-pubkey-info (comp/factory PubkeyInfo))

(defsc Show
  "Show a core node"
  [_this {::m.n.pubkeys/keys [id]
          :ui/keys           [router]
          :as props}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/about        ""
                   ::m.n.pubkeys/display-name ""
                   ::m.n.pubkeys/hex          ""
                   ::m.n.pubkeys/id           nil
                   ::m.n.pubkeys/lud06        ""
                   ::m.n.pubkeys/name         ""
                   ::m.n.pubkeys/nip05        ""
                   ::j.n.pubkeys/npub        ""
                   ::m.n.pubkeys/picture      ""
                   ::m.n.pubkeys/website      ""
                   :ui/router                 {}}
   :pre-merge     (u.links/page-merger ::m.n.pubkeys/id {:ui/router Router})
   :query         [::m.n.pubkeys/about
                   ::m.n.pubkeys/display-name
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/id
                   ::m.n.pubkeys/lud06
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/nip05
                   ::j.n.pubkeys/npub
                   ::m.n.pubkeys/picture
                   ::m.n.pubkeys/website
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["pubkey" :id]
   :will-enter    (partial u.links/page-loader ::m.n.pubkeys/id ::Show)}
  (let [{:keys [main]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (ui-pubkey-info props)
      (u.links/ui-nav-menu {:menu-items menu-items :id id})
      ((comp/factory Router) router))))

(form/defsc-form CreateForm
  [_this _props]
  {fo/attributes   [m.n.pubkeys/hex]
   fo/cancel-route ["pubkeys"]
   fo/id           m.n.pubkeys/id
   fo/route-prefix "create-pubkey"
   fo/title        "Create A Pubkey"})

(def new-button
  {:type   :button
   :local? true
   :label  "New"
   :action (fn [this _] (form/create! this CreateForm))})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.pubkeys/picture
                        m.n.pubkeys/name
                        j.n.pubkeys/contact-count
                        j.n.pubkeys/event-count]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::new     new-button
                        ::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.pubkeys/hex     #(u.links/ui-pubkey-link %3)
                        ::m.n.pubkeys/name    #(u.links/ui-pubkey-name-link %3)
                        ::m.n.pubkeys/picture #(img-formatter %3)}
   ro/route            "pubkeys"
   ro/row-actions      [(u.links/row-action-button "Add to contacts" ::m.n.pubkeys/id mu.n.pubkeys/add-contact!)
                        ;; (u.links/row-action-button "Fetch" ::m.n.pubkeys/id mu.n.pubkeys/fetch!)
                        ;; (u.links/row-action-button "Fetch Contacts" ::m.n.pubkeys/id mu.n.pubkeys/fetch-contacts!)
                        ]
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.pubkeys/index
   ro/title            "Pubkeys"})
