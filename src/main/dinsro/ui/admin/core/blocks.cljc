(ns dinsro.ui.admin.core.blocks
  (:require
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
   [dinsro.ui.admin.core.blocks.transactions :as u.a.c.b.transactions]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/core/blocks.cljc]]
;; [[../../../model/core/blocks.cljc]]

(def force-fetch-button false)
(def index-page-key :admin-core-blocks)
(def model-key ::m.c.blocks/id)
(def show-page-key :admin-core-blocks-show)
(def debug-page false)

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
   ;; :pre-merge     (u.loader/page-merger model-key {:ui/transactions [u.a.c.b.transactions/SubPage {}]})
   :query         [::m.c.blocks/id
                   ::m.c.blocks/height
                   ::m.c.blocks/hash
                   ::m.c.blocks/nonce
                   ::m.c.blocks/weight
                   ::m.c.blocks/fetched?
                   {::m.c.blocks/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.c.blocks/previous-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {::m.c.blocks/next-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {:ui/transactions (comp/get-query u.a.c.b.transactions/SubPage)}
                   [df/marker-table '_]]}
  (log/trace :Show/creating {:id id :props props :this this})
  (dom/div {}
    (ui-segment {}
      (dom/h1 {}
        "Block " height
        (when-not (and (not force-fetch-button) fetched?)
          (comp/fragment
           " ("
           (dom/a {:href    "#"
                   :onClick #(comp/transact! this [`(mu.c.blocks/fetch! {~model-key ~id})])}
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
                                (comp/transact! this [`(mu.c.blocks/fetch! {~model-key ~id})]))}

          "Fetch")))
    (if id
      (dom/div {}
        ((comp/factory u.a.c.b.transactions/SubPage) transactions))
      (dom/div {}
        (dom/p {} "No id")))))

(def ui-show (comp/factory Show))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.blocks/hash #(u.links/ui-admin-block-link %3)}

   ro/columns           [m.c.blocks/hash
                         m.c.blocks/height
                         m.c.blocks/fetched?]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [(u.buttons/row-action-button "Delete" model-key mu.c.blocks/delete!)]
   ro/row-pk            m.c.blocks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.blocks/admin-index
   ro/title             "Admin Core Blocks"})

(def ui-report (comp/factory Report))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["blocks"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id show-page-key
                   ::m.navlinks/target      {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["block" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (if target
    (ui-show target)
    (ui-segment {:color "red" :inverted true}
      "Failed to load page")))

(m.navlinks/defroute index-page-key
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/description   "Admin index blocks"
   ::m.navlinks/label         "Blocks"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    :admin-core
   ::m.navlinks/router        :admin-core
   ::m.navlinks/required-role :admin})

(m.navlinks/defroute show-page-key
  {::m.navlinks/control       ::ShowPage
   ::m.navlinks/description   "Admin show block"
   ::m.navlinks/label         "Show Block"
   ::m.navlinks/input-key     model-key
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    index-page-key
   ::m.navlinks/router        :admin-core
   ::m.navlinks/required-role :admin})
