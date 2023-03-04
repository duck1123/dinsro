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
   [dinsro.mutations.nostr.pubkey-contacts :as mu.n.pubkey-contacts]
   [dinsro.mutations.nostr.pubkey-events :as mu.n.pubkey-events]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.pubkey-contacts :as u.n.pubkey-contacts]
   [dinsro.ui.nostr.pubkey-events :as u.n.pubkey-events]
   [dinsro.ui.nostr.pubkey-relays :as u.n.pubkey-relays]
   [dinsro.ui.nostr.pubkey-users :as u.n.pubkey-users]))

;; [[../../actions/nostr/pubkeys.clj][Pubkey Actions]]
;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../model/nostr/pubkeys.cljc][Pubkeys Model]]
;; [[../../mutations/nostr/pubkey_contacts.cljc][Pubkey Contact Mutations]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkey Mutations]]

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.pubkey-users/SubPage
    u.n.pubkey-relays/SubPage
    u.n.pubkey-contacts/SubPage
    u.n.pubkey-events/SubPage]})

(defn img-formatter
  [pubkey]
  (if-let [picture (::m.n.pubkeys/picture pubkey)]
    (dom/img {:src picture :height 100 :width 100})
    ""))

(def menu-items
  [{:key "contacts" :name "Contacts" :route "dinsro.ui.nostr.pubkey-contacts/SubPage"}
   {:key "users" :name "Users" :route "dinsro.ui.nostr.pubkey-users/SubPage"}
   {:key "relays" :name "Relays" :route "dinsro.ui.nostr.pubkey-relays/SubPage"}
   {:key "events" :name "Events" :route "dinsro.ui.nostr.pubkey-events/SubPage"}])

(defsc Show
  "Show a core node"
  [this {::m.n.pubkeys/keys [id hex name picture about nip05 lud06 website banner]
         :ui/keys           [router]}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/hex     ""
                   ::m.n.pubkeys/name    ""
                   ::m.n.pubkeys/nip05   ""
                   ::m.n.pubkeys/lud06   ""
                   ::m.n.pubkeys/banner  ""
                   ::m.n.pubkeys/about   ""
                   ::m.n.pubkeys/website ""
                   ::m.n.pubkeys/picture ""
                   :ui/router            {}}
   :pre-merge     (u.links/page-merger ::m.n.pubkeys/id {:ui/router Router})
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/about
                   ::m.n.pubkeys/nip05
                   ::m.n.pubkeys/website
                   ::m.n.pubkeys/lud06
                   ::m.n.pubkeys/banner
                   ::m.n.pubkeys/picture
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["pubkey" :id]
   :will-enter    (partial u.links/page-loader ::m.n.pubkeys/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/dl {}
          (dom/dt {} "Pubkey Hex")
          (dom/dd {} (str hex))
          (dom/dt {} "Name")
          (dom/dd {} (str name))
          (dom/dt {} "About")
          (dom/dd {} (str about))
          (dom/dt {} "Nip 05")
          (dom/dd {} (str nip05))
          (dom/dt {} "Website")
          (dom/dd {} (str website))
          (dom/dt {} "lud06")
          (dom/dd {} (str lud06))
          (dom/dt {} "banner")
          (dom/dd {} (str banner))
          (dom/dt {} "Picture")
          (dom/dd {}
            (when picture
              (dom/img {:src    (str picture)
                        :width  200
                        :height 200}))))
        (dom/button
          {:classes [:.ui.button]
           :onClick (fn [_e] (comp/transact! this [(mu.n.pubkeys/fetch! {::m.n.pubkeys/id id})]))}
          "Fetch")
        (dom/button
          {:classes [:.ui.button]
           :onClick (fn [_e] (comp/transact! this [(mu.n.pubkey-contacts/fetch-contacts! {::m.n.pubkeys/id id})]))}
          "Fetch Contacts")
        (dom/button
          {:classes [:.ui.button]
           :onClick (fn [_e] (comp/transact! this [(mu.n.pubkey-events/fetch! {::m.n.pubkeys/id id})]))}
          "Fetch Events"))
      (u.links/ui-nav-menu {:menu-items menu-items :id id})
      ((comp/factory Router) router))))

(form/defsc-form CreateForm
  [_this _props]
  {fo/attributes    [m.n.pubkeys/hex]
   fo/cancel-route  ["pubkeys"]
   fo/id            m.n.pubkeys/id
   fo/route-prefix  "create-pubkey"
   fo/title         "Create A Pubkey"})

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
                        (u.links/row-action-button "Fetch" ::m.n.pubkeys/id mu.n.pubkeys/fetch!)
                        (u.links/row-action-button "Fetch Contacts" ::m.n.pubkeys/id mu.n.pubkeys/fetch-contacts!)]
   ro/row-pk           m.n.pubkeys/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.pubkeys/index
   ro/title            "Pubkeys"})
