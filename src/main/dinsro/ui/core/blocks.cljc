(ns dinsro.ui.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.core.blocks.transactions :as u.c.b.transactions]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/core/blocks.cljc]]
;; [[../../model/core/blocks.cljc]]

(def index-page-id :core-blocks)
(def model-key ::m.c.blocks/id)
(def parent-router-id :core)
(def required-role :user)
(def show-page-id :core-blocks-show)

(def force-fetch-button false)
(def debug-page false)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.c.blocks/delete!))

(defsc RefRow
  [this {::m.c.blocks/keys [fetched? height id] :as props}]
  {:ident ::m.c.blocks/id
   :query [::m.c.blocks/id
           ::m.c.blocks/fetched?
           ::m.c.blocks/hash
           ::m.c.blocks/height]}
  (dom/tr {}
    (dom/td (str fetched?))
    (dom/td (u.links/ui-block-link props))
    (dom/td (str height))
    (dom/td {}
      (dom/button {:classes [:.ui.button]
                   :onClick (fn [event]
                              (log/info :fetch-button/clicked {:event event})
                              (comp/transact! this [`(mu.c.blocks/fetch! {::m.c.blocks/id ~id})]))}
        "Fetch"))))

(defsc CoreBlocksRefRow-wrapper
  [_this props]
  {:ident         :ui/foo
   :initial-state {:ui/foo 1}
   :query         [:ui/foo]}
  (log/info :starting {:props props})
  (dom/div {}
    (dom/div {} "foo")))

(s/def ::row
  (s/keys
   :req [::m.c.blocks/id
         ::m.c.blocks/hash
         ::m.c.blocks/height
         ::m.c.blocks/fetched?]))

(defsc Show
  "Show a core block"
  [this {::m.c.blocks/keys [id height hash previous-block next-block nonce fetched? weight network]
         :ui/keys          [transactions]
         :as               props}]
  {:ident         ::m.c.blocks/id
   :initial-state {::m.c.blocks/id             nil
                   ::m.c.blocks/height         ""
                   ::m.c.blocks/hash           ""
                   ::m.c.blocks/previous-block {}
                   ::m.c.blocks/next-block     {}
                   ::m.c.blocks/weight         0
                   ::m.c.blocks/nonce          ""
                   ::m.c.blocks/fetched?       false
                   ::m.c.blocks/network        {}
                   :ui/transactions            {}}
   :pre-merge         (u.loader/page-merger model-key
                        {:ui/transactions   [u.c.b.transactions/SubSection {}]})
   :query         [::m.c.blocks/id
                   ::m.c.blocks/height
                   ::m.c.blocks/hash
                   ::m.c.blocks/nonce
                   ::m.c.blocks/weight
                   ::m.c.blocks/fetched?
                   {::m.c.blocks/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.c.blocks/previous-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {::m.c.blocks/next-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {:ui/transactions (comp/get-query u.c.b.transactions/SubSection)}
                   [df/marker-table '_]]}
  (log/debug :Show/creating {:props props})
  (if id
    (dom/div {}
      (if id
        (ui-segment {}
          (dom/h1 {}
            "Block " height
            (when-not (and (not force-fetch-button) fetched?)
              (dom/span {}
                " ("
                (dom/a {:href    "#"
                        :onClick #(comp/transact! this [`(mu.c.blocks/fetch! {::m.c.blocks/id ~id})])}
                  "unfetched")
                ")")))
          (dom/dl {}
            (dom/dt {} "Hash")
            (dom/dd {} hash)
            (dom/dt {} "Weight")
            (dom/dd {} weight)
            (dom/dt {} "Nonce")
            (dom/dd {} nonce)
            (dom/dt {} "Network")
            (dom/dd {} (u.links/ui-network-link network))
            (when previous-block
              (comp/fragment
               (dom/dt {} "Previous")
               (dom/dd {} (u.links/ui-block-height-link previous-block))))
            (when next-block
              (comp/fragment
               (dom/dt {} "Next")
               (dom/dd {} (u.links/ui-block-height-link next-block)))))
          (when debug-page
            (dom/button {:onClick (fn [_e]
                                    (log/info :ShowBlock/fetch-button-clicked {})
                                    (comp/transact! this [`(mu.c.blocks/fetch! {::m.c.blocks/id ~id})]))}
              "Fetch")))
        (u.debug/load-error props "record"))
      (if id
        (dom/div {}
          (u.c.b.transactions/ui-sub-section transactions))
        (u.debug/load-error props "no id")))
    (u.debug/load-error props "no id")))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/columns           [m.c.blocks/hash
                         m.c.blocks/height
                         m.c.blocks/fetched?]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/source-attribute  ::j.c.blocks/index
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.c.blocks/id
   ro/run-on-mount?     true
   ro/title             "Core Blocks"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:ident         (fn [] [::m.navlinks/id index-page-id])
   :initial-state {::m.navlinks/id index-page-id
                   :ui/report      {}}
   :query         [::m.navlinks/id
                   {:ui/report (comp/get-query Report)}]
   :route-segment ["blocks"]
   :will-enter    (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query        (fn []
                   [model-key
                    o.navlinks/id
                    {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["block" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Block"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
