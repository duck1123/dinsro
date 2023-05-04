(ns dinsro.ui.nostr.events
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-item :refer [ui-list-item]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.events.event-tags :as u.n.e.event-tags]
   [dinsro.ui.nostr.events.relays :as u.n.e.relays]
   [dinsro.ui.nostr.events.witnesses :as u.n.e.witnesses]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as md.transform]
   [sablono.core :as html :refer-macros [html]]))

;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../joins/nostr/events.cljc][Event Joins]]
;; [[../../mutations/nostr/events.cljc][Event Mutations]]

(def log-event-props false)

(def menu-items
  [{:key   "tags"
    :name  "Tags"
    :route "dinsro.ui.nostr.events.event-tags/SubPage"}
   {:key   "witnesses"
    :name  "Witnesses"
    :route "dinsro.ui.nostr.events.witnesses/SubPage"}
   {:key   "relays"
    :name  "Relays"
    :route "dinsro.ui.nostr.events.relays/SubPage"}])

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.n.events/id]
   fo/cancel-route ["events"]
   fo/id           m.n.events/id
   fo/route-prefix "new-event"
   fo/title        "Event"})

(def new-button
  {:type   :button
   :local? true
   :label  "New Event"
   :action (fn [this _] (form/create! this NewForm))})

(defsc EventAuthorImage
  [_this {::m.n.pubkeys/keys [picture]}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/name    ""
                   ::m.n.pubkeys/picture ""}
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/picture]}
  (dom/div :.ui.container
    (when picture (dom/img {:src picture}))))

(defsc EventAuthor
  [_this {::m.n.pubkeys/keys [picture]}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/picture ""
                   ::m.n.pubkeys/hex     ""
                   ::m.n.pubkeys/nip05   ""}
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/picture
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/nip05]}
  (when picture (dom/img {:src picture :width 100 :height 100})))

(def ui-event-author-image (comp/factory EventAuthorImage))

(defsc TagDisplay
  [_this {::m.n.event-tags/keys [pubkey event index raw-value extra type]}]
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
        tag? (= type "t")]
    (ui-list-item {}
      (dom/div {:style {:marginRight "5px"}} "[" (str index) "] ")
      (when tag?
        (str "#" raw-value))
      (when pubkey
        (dom/div {}
          (when show-labels "Pubkey: ")
          (u.links/ui-pubkey-name-link pubkey)))
      (when event
        (dom/div {}
          (when show-labels "Event: ")
          (u.links/ui-event-link event)))
      (when-not (or pubkey event tag?)
        (comp/fragment
         (dom/div {} "Type: " (str type))
         (dom/div {} "Raw Value: " (str raw-value))))
      (when extra (dom/div {} "Extra: " (str extra))))))

(def ui-tag-display (comp/factory TagDisplay {:keyfn ::m.n.event-tags/id}))

(def log-run-props false)
(def log-witness-props false)
(def log-connection-props true)

(defsc ConnectionDisplay
  [_this {::m.n.connections/keys [relay] :as props}]
  {:ident         ::m.n.connections/id
   :initial-state {::m.n.connections/id     nil
                   ::m.n.connections/status :initial
                   ::m.n.connections/relay  {}}
   :query         [::m.n.connections/id
                   ::m.n.connections/status
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}]}
  (dom/div {} "foo")
  (u.links/ui-relay-link props)
  (u.links/ui-relay-link relay))

(def ui-connection-display (comp/factory ConnectionDisplay {:keyfn ::m.n.connections/id}))

(defsc RunDisplay
  [_this {::m.n.runs/keys [connection] :as props}]
  {:ident         ::m.n.runs/id
   :initial-state {::m.n.runs/id         nil
                   ::m.n.runs/status {}
                   ::m.n.runs/connection {}}
   :query         [::m.n.runs/id
                   ::m.n.runs/status
                   {::m.n.runs/connection (comp/get-query ConnectionDisplay)}]}
  (dom/div {} (u.links/ui-run-link props))
  (ui-connection-display connection))

(def ui-run-display (comp/factory RunDisplay {:keyfn ::m.n.runs/id}))

(defsc WitnessDisplay
  [_this {::m.n.witnesses/keys [run] :as props}]
  {:ident         ::m.n.witnesses/id
   :query         [::m.n.witnesses/id
                   {::m.n.witnesses/run (comp/get-query RunDisplay)}]
   :initial-state {::m.n.witnesses/id  nil
                   ::m.n.witnesses/run {}}}
  (ui-list-item {}
    (when log-witness-props (dom/div {} (u.links/ui-witness-link props)))
    (ui-run-display run)))

(def ui-witness-display (comp/factory WitnessDisplay {:keyfn ::m.n.witnesses/id}))

(def transform-markup true)
(def convert-html true)
(def show-ast false)

(defn replace-images
  [ast]
  (let [{:keys [content type]} ast]
    (if (= type :link)
      (let [src (get-in ast [:attrs :href])]
        {:type :image :content [] :attrs {:src src :alt src}})
      (let [transformed-content (mapv replace-images content)]
        (assoc ast :content transformed-content)))))

(def transformer
  (assoc md.transform/default-hiccup-renderers
         ;; :doc specify a custom container for the whole doc
         :doc (partial md.transform/into-markup [:div.viewer-markdown])
         :image (fn [_ctx {{:keys [alt src]} :attrs}]
                  [:a {:href src} [:img.ui.fluid.image {:alt alt :src src}]])
         ;; :text is funkier when it's zinc toned
         :text (fn [_ctx node] [:span {:style {:color "#71717a"}} (:text node)])
         ;; :plain fragments might be nice, but paragraphs help when no reagent is at hand
         :plain (partial md.transform/into-markup [:p {:style {:margin-top "-1.2rem"}}])
         ;; :ruler gets to be funky, too
         :ruler (constantly [:hr {:style {:border "2px dashed #71717a"}}])))

(defsc EventBox
  [_this {::m.n.events/keys [content pubkey kind]
          ::j.n.events/keys [created-date tags witnesses]}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id           nil
                   ::m.n.events/pubkey       {}
                   ::m.n.events/content      ""
                   ::m.n.events/kind         0
                   ::m.n.events/created-at   0
                   ::j.n.events/created-date nil
                   ::j.n.events/witnesses    []
                   ::j.n.events/tags         []}
   :query         [::m.n.events/id
                   ::m.n.events/content
                   ::m.n.events/created-at
                   ::m.n.events/kind
                   ::j.n.events/created-date
                   {::j.n.events/witnesses (comp/get-query WitnessDisplay)}
                   {::m.n.events/pubkey (comp/get-query EventAuthor)}
                   {::j.n.events/tags (comp/get-query TagDisplay)}]}
  (dom/div :.ui.item.segment.event-box
    (dom/div :.ui.tiny.image
      (ui-event-author-image pubkey))
    (dom/div :.content
      (dom/div {:classes [:.header] :style {:width "100%"}}
        (ui-grid {}
          (ui-grid-row {}
            (ui-grid-column {:stretched true :width 10} (u.links/ui-pubkey-name-link pubkey))
            (ui-grid-column {:textAlign "right" :width 6} (str (::m.n.pubkeys/nip05 pubkey))))))
      (dom/div {:classes [:.meta] :style {:width "100%"}}
        (ui-grid {}
          (ui-grid-row {}
            (ui-grid-column {:width 13}
              (str created-date))
            (ui-grid-column {:floated "right" :textAlign "right" :width 2}
              (str kind)))))
      (dom/div {:classes [:.description]}
        (dom/div :.ui.container
          (condp = kind
            0 (dom/div :.ui.container
                (dom/div {:style {:width "100%" :overflow "auto"}}
                  (dom/code {}
                    (dom/pre {} content))))
            (let [ast (replace-images (md/parse content))]
              (comp/fragment
               (if show-ast
                 (u.links/log-props ast)
                 (if transform-markup
                   (let [hiccup (md.transform/->hiccup transformer ast)]
                     (if convert-html
                       (html hiccup)
                       (str hiccup)))
                   (str content))))))))
      (dom/div :.extra.content
        (when (seq tags)
          (ui-segment {}
            (dom/div :.ui.relaxed.divided.list
              (map ui-tag-display (sort-by ::m.n.event-tags/index tags)))))
        (when (seq witnesses)
          (ui-segment {}
            (dom/div :.ui.relaxed.divided.list
              (map ui-witness-display witnesses))))))))

(def ui-event-box (comp/factory EventBox {:keyfn ::m.n.events/id}))

(def override-report false)
(def show-controls true)

(report/defsc-report Report
  [this props]
  {ro/BodyItem          EventBox
   ro/column-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                         ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/columns           [m.n.events/content]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "events"
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.events/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.events/index
   ro/title             ""}
  (if override-report
    (report/render-layout this)
    (let [{:ui/keys [current-rows]} props]
      (dom/div :.ui.grid.center.event-report
        (dom/div :.ui.row.center.text.align
          (dom/div :.ui.column
            (dom/div :.ui.segment
              (dom/h1 :.ui.header "Events"))))
        (dom/div :.ui.row
          (dom/div :.ui.column
            (dom/div {:classes [:.ui :.container]}
              (dom/div :.ui.segment
                (ui-button {:icon    "refresh"
                            :onClick (fn [_] (control/run! this))})
                (when show-controls ((report/control-renderer this) this))
                (dom/div {:classes [:.ui :.unstackable :.divided :.items :.center :.aligned]}
                  (map ui-event-box current-rows))))))))))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.e.event-tags/SubPage
    u.n.e.relays/SubPage
    u.n.e.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.events/keys [id content pubkey kind sig created-at note-id]
          :ui/keys          [router]}]
  {:ident         ::m.n.events/id
   :initial-state {::m.n.events/id         nil
                   ::m.n.events/note-id    ""
                   ::m.n.events/content    ""
                   ::m.n.events/pubkey     {}
                   ::m.n.events/kind       nil
                   ::m.n.events/created-at 0
                   ::m.n.events/sig        ""
                   :ui/router              {}}
   :pre-merge     (u.links/page-merger ::m.n.events/id {:ui/router Router})
   :query         [::m.n.events/id
                   ::m.n.events/content
                   {::m.n.events/pubkey (comp/get-query EventAuthorImage)}
                   ::m.n.events/kind
                   ::m.n.events/note-id
                   ::m.n.events/created-at
                   ::m.n.events/sig
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["event" :id]
   :will-enter    (partial u.links/page-loader ::m.n.events/id ::Show)}
  (dom/div :.ui.segment
    (dom/div :.ui.segment
      (dom/div :.ui.items.unstackable
        (dom/div :.item
          (dom/div :.ui.tiny.image
            (ui-event-author-image pubkey))
          (dom/div :.content
            (dom/div {:classes [:.header]}
              (u.links/ui-pubkey-name-link pubkey))
            (dom/div {:classes [:.meta]}
              (dom/span {:classes [:.date]}
                        (str created-at) " - " (str kind)))
            (dom/div {:classes [:.description]}
              (str content))
            (dom/div {} "Sig: " (str sig))
            (dom/div {} "Note Id: " (str note-id))))))
    (u.links/ui-nav-menu {:menu-items menu-items :id id})
    ((comp/factory Router) router)))
