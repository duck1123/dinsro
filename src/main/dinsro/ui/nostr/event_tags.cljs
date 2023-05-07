(ns dinsro.ui.nostr.event-tags
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [dinsro.menus :as me]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.event-tags.relays :as u.n.et.relays]
   [lambdaisland.glogc :as log]))

;; [[../../ui/nostr/event_tags/relays.cljs]]
;; [[../../../../test/dinsro/ui/nostr/event_tags_test.cljs]]

(def log-tag-props false)

(defsc TagDisplay
  [_this {::m.n.event-tags/keys [id pubkey event raw-value extra type] :as props}]
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
  (if log-tag-props
    (u.links/log-props props)
    (let [show-labels false
          tag?        (= type "t")
          event?      (= type "e")]
      (ui-list-item {}
        (when log-tag-props
          (u.links/log-props props))
        (dom/div {:style {:marginRight "5px"}}
          "[" (u.links/ui-event-tag-link props) "] ")
        (when tag?
          (str "#" raw-value))
        (when pubkey
          (dom/div {}
            (when show-labels "Pubkey: ")
            (u.links/ui-pubkey-name-link pubkey)))
        (when event?
          (dom/div {}
            (when show-labels "Event: ")
            (when event (u.links/ui-event-link event))))
        (when-not (or pubkey event tag?)
          (comp/fragment
           (dom/div {} "Type: " (str type))
           (dom/div {} "Raw Value: " (str raw-value))))
        (when extra (dom/div {} "Extra: " (str extra)))
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
          :ui/keys              [router]}]
  {:ident         ::m.n.event-tags/id
   :initial-state {::m.n.event-tags/id        nil
                   ::m.n.event-tags/index     0
                   ::m.n.event-tags/type      ""
                   ::m.n.event-tags/raw-value ""
                   ::m.n.event-tags/pubkey    {}
                   :ui/router                 {}}
   :pre-merge     (u.links/page-merger ::m.n.event-tags/id {:ui/router Router})
   :query         [::m.n.event-tags/id
                   ::m.n.event-tags/index
                   ::m.n.event-tags/type
                   ::m.n.event-tags/raw-value
                   {::m.n.event-tags/pubkey (comp/get-query u.links/EventTagLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["event-tag" :id]
   :will-enter    (partial u.links/page-loader ::m.n.event-tags/id ::Show)}
  (dom/div :.ui.segment
    (dom/div :.ui.segment
      (dom/div :.ui.items.unstackable
        (dom/div :.item
          (dom/div {}
            (str id)
            (dom/p {} (str index))
            (dom/p {} (str type))
            (dom/p {} (str raw-value))
            (dom/p {} (u.links/ui-event-tag-link pubkey))))))
    (u.links/ui-nav-menu {:menu-items me/nostr-event-tags-menu-items :id id})
    ((comp/factory Router) router)))

(def ui-show (comp/factory Show))
