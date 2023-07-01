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
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
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

(def ui-router (comp/factory Router))

(def show-border false)

(defsc PubkeyInfo
  [_this {::j.n.pubkeys/keys [npub]
          ::m.n.pubkeys/keys [about display-name hex lud06 name nip05 picture website]}]
  {:css           [[:.content-box {:overflow "hidden"}]
                   [:.info {}]
                   [:.picture-container {}]]
   :ident         ::m.n.pubkeys/id
   :initial-state {::j.n.pubkeys/npub         ""
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
  (let [avatar-size                                  200
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
              (dom/div {} (when website (dom/a {:href website} (str website))))
              (dom/div {} (str lud06)))))))))

(def ui-pubkey-info (comp/factory PubkeyInfo))

(defsc Show
  "Show a core node"
  [_this {:ui/keys           [nav-menu router]
          :as props}]
  {:ident         ::m.n.pubkeys/id
   :initial-state (fn [props]
                    (let [id (::m.n.pubkeys/id props)]
                      {::m.n.pubkeys/about        ""
                       ::m.n.pubkeys/display-name ""
                       ::m.n.pubkeys/hex          ""
                       ::m.n.pubkeys/id           nil
                       ::m.n.pubkeys/lud06        ""
                       ::m.n.pubkeys/name         ""
                       ::m.n.pubkeys/nip05        ""
                       ::j.n.pubkeys/npub         ""
                       ::m.n.pubkeys/picture      ""
                       ::m.n.pubkeys/website      ""
                       :ui/nav-menu (comp/get-initial-state u.menus/NavMenu
                                                            {::m.navbars/id :nostr-pubkeys :id id})
                       :ui/router                 (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger ::m.n.pubkeys/id {:ui/router [Router {}]})
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
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["pubkey" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.pubkeys/id ::Show)}
  (let [{:keys [main]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (ui-pubkey-info props)
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))))

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
  {ro/column-formatters {::m.n.pubkeys/name    #(u.links/ui-pubkey-name-link %3)
                         ::m.n.pubkeys/picture #(u.links/img-formatter %3)}
   ro/columns           [m.n.pubkeys/picture
                         m.n.pubkeys/name
                         m.n.pubkeys/hex
                         j.n.pubkeys/contact-count
                         j.n.pubkeys/event-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::new     new-button
                         ::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "pubkeys"
   ro/row-actions       [(u.buttons/row-action-button "Add to contacts" ::m.n.pubkeys/id mu.n.pubkeys/add-contact!)]
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/index
   ro/title             "Pubkeys"})
