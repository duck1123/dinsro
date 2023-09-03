(ns dinsro.ui.nostr.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header :refer [ui-table-header]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-header-cell :refer [ui-table-header-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.mutations.nostr.event-tags :as mu.n.event-tags]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.event-tags.relays :as u.n.et.relays]
   [lambdaisland.glogc :as log]))

;; [[../../mutations/nostr/event_tags.cljc]]
;; [[../../ui/nostr/event_tags/relays.cljs]]
;; [[../../../../test/dinsro/ui/nostr/event_tags_test.cljs]]

(def model-key ::m.n.event-tags/id)
(def parent-router-id :nostr)
(def required-role :user)
(def show-page-id :nostr-event-tags-show)

(def log-tag-props false)
(def log-tag-table false)
(def display-tags? true)

(defsc TagDisplay
  [this {::m.n.event-tags/keys [pubkey event raw-value extra type]
         :as                   props}]
  {:query         [::m.n.event-tags/id
                   {::m.n.event-tags/pubkey (comp/get-query u.links/PubkeyNameLinkForm)}
                   {::m.n.event-tags/event (comp/get-query u.links/ui-event-link)}
                   ::m.n.event-tags/index
                   ::m.n.event-tags/raw-value
                   ::m.n.event-tags/extra
                   ::m.n.event-tags/type]
   :ident         ::m.n.event-tags/id
   :initial-state {::m.n.event-tags/id        nil
                   ::m.n.event-tags/pubkey    {}
                   ::m.n.event-tags/event     {}
                   ::m.n.event-tags/index     0
                   ::m.n.event-tags/raw-value nil
                   ::m.n.event-tags/extra     nil
                   ::m.n.event-tags/type      nil}}
  (let [tag?       (= type "t")
        event?     (= type "e")
        pubkey?    (= type "p")
        nonce?     (= type "nonce")
        client?    (= type "client")
        reference? (= type "r")
        has-extra? (and extra (not (or nonce? client? event?)))
        has-type?  (not (or pubkey event tag? nonce? client?))]
    (ui-list-item {}
      (when log-tag-props
        (u.debug/log-props props))

      (when display-tags?
        (dom/div {}
          (when (and event? event)
            (u.links/ui-event-link event))
          (when (and event? (not event))
            (dom/div {}
              (if (seq extra)
                (u.buttons/action-button `mu.n.event-tags/fetch!
                                         (str raw-value " - " extra) model-key this)
                (str "No relay listed: " raw-value))))
          (when (and pubkey? pubkey)
            (u.links/ui-pubkey-name-link pubkey))
          (when (and pubkey? (not pubkey))
            (str "unknown pubkey"))
          (when client?
            (str "Client: " raw-value))
          (when tag?
            (str "#" raw-value))
          (when reference?
            (str "ref: " raw-value))
          (when (not (or event? pubkey? tag? client? reference?))
            (dom/div {}
              (dom/div {} (str "Unknown type: " type))
              (u.debug/log-props props)))))

      (when log-tag-table
        (ui-table {}
          (ui-table-header {}
            (ui-table-row {}
              (ui-table-header-cell {} "key")
              (ui-table-header-cell {} "value")))
          (ui-table-body {}
            (ui-table-row {}
              (ui-table-cell {} "index")
              (ui-table-cell {} (u.links/ui-event-tag-link props)))
            (when tag?
              (ui-table-row {}
                (ui-table-cell {} "tag")
                (ui-table-cell {} (str "#" raw-value))))
            (when event?
              (ui-table-row {}
                (ui-table-cell {} "Event")
                (ui-table-cell {}
                  (if event
                    (u.links/ui-event-link event)
                    (u.buttons/action-button `mu.n.event-tags/fetch! raw-value model-key this)))))
            (when has-type?
              (ui-table-row {}
                (ui-table-cell {} "Type")
                (ui-table-cell {} (str type))))
            (when has-type?
              (ui-table-row {}
                (ui-table-cell {} "Raw Value")
                (ui-table-cell {} (str raw-value))))
            (when client?
              (ui-table-row {}
                (ui-table-cell {} "Client")
                (ui-table-cell {} (str raw-value))))
            (when nonce?
              (ui-table-row {}
                (ui-table-cell {} "POW")
                (ui-table-cell {} (str extra))))
            (when pubkey
              (ui-table-row {}
                (ui-table-cell {} "Pubkey")
                (ui-table-cell {} (u.links/ui-pubkey-name-link pubkey))))
            (when has-extra?
              (ui-table-row {}
                (ui-table-cell {} "Extra")
                (ui-table-cell {} (str extra))))))))))

(def ui-tag-display (comp/factory TagDisplay {:keyfn ::m.n.event-tags/id}))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.et.relays/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/children
   [u.n.et.relays/index-page-id]})

(defsc Show
  [_this {::m.n.event-tags/keys [id index type raw-value pubkey]
          :ui/keys              [nav-menu router]
          :as                   props}]
  {:ident         ::m.n.event-tags/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {::m.n.event-tags/id        nil
                       ::m.n.event-tags/index     0
                       ::m.n.event-tags/type      ""
                       ::m.n.event-tags/raw-value ""
                       ::m.n.event-tags/pubkey    {}
                       :ui/nav-menu               (comp/get-query u.menus/NavMenu {::m.navbars/id show-page-id :id id})
                       :ui/router                 (comp/get-query Router)}))
   :pre-merge     (u.loader/page-merger ::m.n.event-tags/id
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]
                     :ui/router   [Router {}]})
   :query         [::m.n.event-tags/id
                   ::m.n.event-tags/index
                   ::m.n.event-tags/type
                   ::m.n.event-tags/raw-value
                   {::m.n.event-tags/pubkey (comp/get-query u.links/EventTagLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (ui-segment {}
      (ui-segment {}
        (dom/div :.ui.items.unstackable
          (dom/div :.item
            (dom/div {}
              (str id)
              (dom/p {} (str index))
              (dom/p {} (str type))
              (dom/p {} (str raw-value))
              (dom/p {} (u.links/ui-event-tag-link pubkey))))))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))
    (u.debug/load-error props "event tags")))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state {::m.n.event-tags/id nil
                   ::m.navlinks/id     show-page-id
                   ::m.navlinks/target {}}
   :query         [::m.n.event-tags/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["event-tag" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Event Tag"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
