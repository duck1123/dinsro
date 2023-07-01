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
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.controls :refer [ui-moment]]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]
   [dinsro.ui.nostr.events.event-tags :as u.n.e.event-tags]
   [dinsro.ui.nostr.events.relays :as u.n.e.relays]
   [dinsro.ui.nostr.events.witnesses :as u.n.e.witnesses]
   [dinsro.ui.nostr.witnesses :as u.n.witnesses]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as md.transform]
   [sablono.core :as html :refer-macros [html]]))

;; [[../../queries/nostr/events.clj][Event Queries]]
;; [[../../joins/nostr/events.cljc][Event Joins]]
;; [[../../mutations/nostr/events.cljc][Event Mutations]]
;; [[../../ui/nostr.cljs]]
;; [[../../ui/nostr/pubkeys/events.cljs]]

(def log-event-props false)

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

(def debug-tags true)

(def log-run-props false)
(def log-connection-props true)

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
                   {::j.n.events/witnesses (comp/get-query u.n.witnesses/WitnessDisplay)}
                   {::m.n.events/pubkey (comp/get-query EventAuthor)}
                   {::j.n.events/tags (comp/get-query u.n.event-tags/TagDisplay)}]}
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
              (ui-moment {:fromNow true :withTitle true}
                (str created-date)))
            (ui-grid-column {:floated "right" :textAlign "right" :width 2}
              (str kind)))))
      (dom/div {:classes [:.description]}
        (when (seq tags)
          (ui-segment {}
            (dom/div :.ui.relaxed.divided.list
              (let [pubkey-tags (filter
                                 (fn [tag] (= "p" (::m.n.event-tags/type tag)))
                                 (sort-by ::m.n.event-tags/index tags))]
                (map u.n.event-tags/ui-tag-display pubkey-tags)))))
        (dom/div :.ui.container
          (condp = kind
            0 (dom/div :.ui.container
                (dom/div {:style {:width "100%" :overflow "auto"}}
                  (dom/code {}
                    (dom/pre {} content))))
            (let [ast (replace-images (md/parse content))]
              (comp/fragment
               (if show-ast
                 (u.debug/log-props ast)
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
              (let [filtered-tags (filter
                                   (fn [tag] (not= "p" (::m.n.event-tags/type tag)))
                                   (sort-by ::m.n.event-tags/index tags))]
                (map u.n.event-tags/ui-tag-display filtered-tags)))))
        (when (seq witnesses)
          (ui-segment {}
            (dom/div :.ui.relaxed.divided.list
              (map u.n.witnesses/ui-witness-display witnesses))))))))

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
  [_this {::m.n.events/keys [content pubkey kind sig created-at note-id]
          :ui/keys          [nav-menu router]}]
  {:ident         ::m.n.events/id
   :initial-state
   (fn [props]
     (let [id (::m.n.events/id props)]
       {::m.n.events/id         nil
        ::m.n.events/note-id    ""
        ::m.n.events/content    ""
        ::m.n.events/pubkey     {}
        ::m.n.events/kind       nil
        ::m.n.events/created-at 0
        ::m.n.events/sig        ""
        :ui/nav-menu
        (comp/get-initial-state
         u.menus/NavMenu
         {::m.navbars/id :nostr-events :id id})
        :ui/router              (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger ::m.n.events/id {:ui/router [Router {}]})
   :query         [::m.n.events/id
                   ::m.n.events/content
                   {::m.n.events/pubkey (comp/get-query EventAuthorImage)}
                   ::m.n.events/kind
                   ::m.n.events/note-id
                   ::m.n.events/created-at
                   ::m.n.events/sig
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["event" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.events/id ::Show)}
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
    (u.menus/ui-nav-menu nav-menu)
    (ui-router router)))
