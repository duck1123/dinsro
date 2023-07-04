(ns dinsro.ui.nostr.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.mutations.nostr.event-tags :as mu.n.event-tags]
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
(def show-page-key :nostr-event-tags-show)

(def log-tag-props false)

(defsc TagDisplay
  [this {::m.n.event-tags/keys [id pubkey event raw-value extra type] :as props}]
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
  (let [show-labels false
        tag?        (= type "t")
        event?      (= type "e")
        nonce?      (= type "nonce")
        client?     (= type "client")]
    (ui-list-item {}
      (when log-tag-props
        (u.debug/log-props props))
      (dom/div {:style {:marginRight "5px"}}
        "[" (u.links/ui-event-tag-link props) "] ")
      (dom/div {}
        (when tag?
          (str "#" raw-value))
        (when client?
          (str "Client: " raw-value))
        (when nonce?
          (str "POW=" extra))
        (when pubkey
          (dom/div {}
            (when show-labels "Pubkey: ")
            (u.links/ui-pubkey-name-link pubkey)
            (ui-button
             {:onClick
              (fn [_]
                (let [props (comp/props this)
                      id    (model-key props)]
                  (comp/transact! this [(mu.n.event-tags/fetch! {model-key id})])))}
             "Fetch")))
        (when event?
          (dom/div {}
            (when show-labels
              "Event: ")
            (if event
              (u.links/ui-event-link event)
              (dom/div {}
                "Not Loaded"
                (ui-button
                 {:onClick (fn [_]
                             (let [props (comp/props this)
                                   id    (model-key props)]
                               (comp/transact! this [(mu.n.event-tags/fetch! {model-key id})])))}
                 "Register")))))

        (when-not (or pubkey event tag? nonce? client?)
          (comp/fragment
           (dom/div {} "Type: " (str type))
           (dom/div {} "Raw Value: " (str raw-value))))
        (when (and extra (not (or nonce? client?)))
          (dom/div {} "Extra: " (str extra)))
        (when event?
          (ui-button
           {:onClick (fn [_]
                       (log/info :TagDisplay/fetch {:extra extra :raw-value raw-value :id id}))}
           "Fetch"))))))

(def ui-tag-display (comp/factory TagDisplay {:keyfn ::m.n.event-tags/id}))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.et.relays/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.event-tags/keys [id index type raw-value pubkey]
          :ui/keys              [nav-menu router]
          :as                   props}]
  {:ident         ::m.n.event-tags/id
   :initial-state (fn [props]
                    (let [id (::m.n.event-tags/id props)]
                      {::m.n.event-tags/id        nil
                       ::m.n.event-tags/index     0
                       ::m.n.event-tags/type      ""
                       ::m.n.event-tags/raw-value ""
                       ::m.n.event-tags/pubkey    {}
                       :ui/nav-menu               (comp/get-query u.menus/NavMenu {::m.navbars/id :nostr-event-tags :id id})
                       :ui/router                 (comp/get-query Router)}))
   :pre-merge     (u.loader/page-merger ::m.n.event-tags/id
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :nostr-event-tags}]
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
    (ui-segment {:color "red" :inverted true}
      "Failed to load record")))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this {::m.n.event-tags/keys [id]
          ::m.navlinks/keys     [target]
          :as                   props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.n.event-tags/id nil
                   ::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.n.event-tags/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["event-tag" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (ui-segment {} "Failed to load record")))
