(ns dinsro.ui.nostr.events
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.addons.pagination.ui-pagination :as sui-pagination]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid :refer [ui-grid]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-column :refer [ui-grid-column]]
   [com.fulcrologic.semantic-ui.collections.grid.ui-grid-row :refer [ui-grid-row]]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [com.fulcrologic.semantic-ui.elements.list.ui-list-list :refer [ui-list-list]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]
   [dinsro.ui.nostr.events.event-tags :as u.n.e.event-tags]
   [dinsro.ui.nostr.events.relays :as u.n.e.relays]
   [dinsro.ui.nostr.events.witnesses :as u.n.e.witnesses]
   [dinsro.ui.nostr.witnesses :as u.n.witnesses]
   [lambdaisland.glogc :as log]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as md.transform]
   [sablono.core :as html :refer-macros [html]]))

;; [[../../queries/nostr/events.clj]]
;; [[../../joins/nostr/events.cljc]]
;; [[../../mutations/nostr/events.cljc]]
;; [[../../ui/admin/nostr/events.cljc]]
;; [[../../ui/nostr.cljs]]
;; [[../../ui/nostr/pubkeys/events.cljs]]

(def index-page-id :nostr-events)
(def model-key ::m.n.events/id)
(def required-role :user)
(def parent-router-id :nostr)
(def show-page-id :nostr-events-show)

(def show-witnesses? false)

(form/defsc-form NewForm [_this _props]
  {fo/attributes   [m.n.events/id]
   fo/cancel-route ["events"]
   fo/id           m.n.events/id
   fo/route-prefix "new-event"
   fo/title        "Event"})

(defsc EventAuthorImage
  [_this {::m.n.pubkeys/keys [picture]}]
  {:ident         ::m.n.pubkeys/id
   :initial-state {::m.n.pubkeys/id      nil
                   ::m.n.pubkeys/name    ""
                   ::m.n.pubkeys/picture ""}
   :query         [::m.n.pubkeys/id
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/picture]}
  (ui-container {}
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
  {:css           [[:.header {:width "100%"}]
                   [:.meta {:width "100%"}]
                   [:.kind0-div {:width "100%" :overflow "auto"}]]
   :ident         ::m.n.events/id
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
  (let [{:keys [header meta kind0-div]} (css/get-classnames EventBox)
        pubkey-tags                     (filter
                                         (fn [tag] (= "p" (::m.n.event-tags/type tag)))
                                         (sort-by ::m.n.event-tags/index tags))]
    (dom/div :.ui.item.segment.event-box
      (dom/div :.ui.tiny.image
        (ui-event-author-image pubkey))
      (dom/div :.content
        (dom/div {:classes [header]}
          (ui-grid {}
            (ui-grid-row {}
              (ui-grid-column {:stretched true :width 10}
                (u.links/ui-pubkey-name-link pubkey))
              (ui-grid-column {:textAlign "right" :width 6}
                (str (::m.n.pubkeys/nip05 pubkey))))))
        (dom/div {:classes [meta]}
          (ui-grid {}
            (ui-grid-row {}
              (ui-grid-column {:width 13}
                (u.controls/relative-date created-date))
              (ui-grid-column {:floated "right" :textAlign "right" :width 2}
                (str kind)))))
        (dom/div {:classes [:.description]}
          (when (seq pubkey-tags)
            (ui-segment {:className [:.pubkey-tags]}
              (dom/div :.ui.relaxed.divided.list
                (map u.n.event-tags/ui-tag-display pubkey-tags))))
          (ui-container {}
            (condp = kind
              0 (ui-container {:className [:kind0]}
                  (dom/div {:classes [kind0-div]}
                    (dom/code {}
                      (dom/pre {} content))))
              (let [ast (replace-images (md/parse content))]
                (comp/fragment
                 (if show-ast
                   (u.debug/log-props ast)
                   (if transform-markup
                     (let [hiccup (md.transform/->hiccup transformer ast)]
                       (if convert-html
                         #?(:cljs (html hiccup)
                            :clj (do (comment html)
                                     (str hiccup)))
                         (str hiccup)))
                     (str content))))))))
        (dom/div :.extra.content
          (when (> (- (count tags) (count pubkey-tags)) 0)
            (ui-segment {:className [:tag-content]}
              (ui-list-list {}
                (let [filtered-tags (filter
                                     (fn [tag] (not= "p" (::m.n.event-tags/type tag)))
                                     (sort-by ::m.n.event-tags/index tags))]
                  (map u.n.event-tags/ui-tag-display filtered-tags)))))
          (when (and show-witnesses? (seq witnesses))
            (ui-segment {}
              (dom/div :.ui.relaxed.divided.list
                (map u.n.witnesses/ui-witness-display witnesses)))))))))

(def ui-event-box (comp/factory EventBox {:keyfn ::m.n.events/id}))

(def override-report false)
(def show-controls false)

(report/defsc-report Report
  [this props]
  {ro/BodyItem          EventBox
   ro/column-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                         ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/columns           [m.n.events/content]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
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
      (ui-grid {:centered true :className "event-report"}
        (ui-grid-row {:centered true :textAlign "center"}
          (ui-grid-column {}
            (ui-segment {}
              (dom/h1 :.ui.header "Events"))))
        (ui-grid-row {}
          (ui-grid-column {}
            (ui-container {}
              (ui-segment {}
                (ui-button {:icon    "refresh"
                            :onClick (fn [_] (control/run! this))})
                (when show-controls ((report/control-renderer this) this))
                (let [page-count (report/page-count this)]
                  (sui-pagination/ui-pagination
                   {:activePage   (report/current-page this)
                    :onPageChange (fn [_ data]
                                    (report/goto-page! this (comp/isoget data "activePage")))
                    :totalPages   page-count
                    :size         "tiny"}))
                (dom/div {:classes [:.ui :.unstackable :.divided :.items :.center :.aligned]}
                  (map ui-event-box current-rows))))))))))

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.e.event-tags/SubPage
    u.n.e.relays/SubPage
    u.n.e.witnesses/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/children
   [u.n.e.event-tags/index-page-id
    u.n.e.witnesses/index-page-id
    u.n.e.relays/index-page-id]})

(defsc Show
  [_this {::m.n.events/keys [content id pubkey kind sig created-at note-id]
          :ui/keys          [nav-menu router]
          :as               props}]
  {:ident         ::m.n.events/id
   :initial-state (fn [props]
                    (let [id (::m.n.events/id props)]
                      {::m.n.events/id         nil
                       ::m.n.events/note-id    ""
                       ::m.n.events/content    ""
                       ::m.n.events/pubkey     {}
                       ::m.n.events/kind       nil
                       ::m.n.events/created-at 0
                       ::m.n.events/sig        ""
                       :ui/nav-menu            (comp/get-initial-state u.menus/NavMenu
                                                 {::m.navbars/id show-page-id
                                                  :id            id})
                       :ui/router              (comp/get-initial-state Router)}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/router   [Router {}]
                     :ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-page-id}]})
   :query         [::m.n.events/id
                   ::m.n.events/content
                   {::m.n.events/pubkey (comp/get-query EventAuthorImage)}
                   ::m.n.events/kind
                   ::m.n.events/note-id
                   ::m.n.events/created-at
                   ::m.n.events/sig
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (if id
    (ui-segment {}
      (ui-segment {}
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
      (ui-router router))
    (u.debug/load-error props "show nostr event record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report)
   :ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["events"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (log/info :Page/starting {:props props})
  (dom/div {}
    (if report
      (ui-report report)
      (u.debug/load-error props "index nostr events page"))))

(defsc ShowPage
  [_this {::m.n.events/keys [id]
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-id])
   :initial-state {::m.n.events/id     nil
                   ::m.navlinks/id     show-page-id
                   ::m.navlinks/target {}}
   :query         [::m.n.events/id
                   ::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["event" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "show nostr event page")))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Events"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Event"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
