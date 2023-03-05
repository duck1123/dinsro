(ns dinsro.ui.core.blocks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.blocks :as j.c.blocks]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.mutations.core.blocks :as mu.c.blocks]
   [dinsro.ui.core.block-transactions :as u.c.block-transactions]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def force-fetch-button false)
(def debug-page false)

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
                                    (comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))}
              "Fetch"))))

(defn delete-action
  [report-instance {::m.c.nodes/keys [id]}]
  (form/delete! report-instance ::m.c.blocks/id id))

(def delete-action-button
  {:action delete-action
   :label  "Delete"
   :style  :delete-button})

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
   :pre-merge     (u.links/page-merger ::m.c.blocks/id {:ui/transactions u.c.block-transactions/SubPage})
   :query         [::m.c.blocks/id
                   ::m.c.blocks/height
                   ::m.c.blocks/hash
                   ::m.c.blocks/nonce
                   ::m.c.blocks/weight
                   ::m.c.blocks/fetched?
                   {::m.c.blocks/network (comp/get-query u.links/NetworkLinkForm)}
                   {::m.c.blocks/previous-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {::m.c.blocks/next-block (comp/get-query u.links/BlockHeightLinkForm)}
                   {:ui/transactions (comp/get-query u.c.block-transactions/SubPage)}
                   [df/marker-table '_]]
   :route-segment ["blocks" :id]
   :will-enter    (partial u.links/page-loader ::m.c.blocks/id ::Show)}
  (log/finer :Show/creating {:id id :props props :this this})
  (dom/div {}
    (dom/div :.ui.segment
      (dom/h1 {}
              "Block " height
              (when-not (and (not force-fetch-button) fetched?)
                (comp/fragment
                 " ("
                 (dom/a {:href    "#"
                         :onClick #(comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})])}
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
                                (comp/transact! this [(mu.c.blocks/fetch! {::m.c.blocks/id id})]))}

          "Fetch")))
    (if id
      (dom/div {}
        (u.c.block-transactions/ui-sub-page transactions))
      (dom/div {}
        (dom/p {} "No id")))))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.blocks/hash
                        m.c.blocks/height
                        m.c.blocks/fetched?]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/route            "blocks"
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.blocks/index
   ro/title            "Core Blocks"})

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.c.blocks/hash
                        m.c.blocks/height
                        m.c.blocks/fetched?]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.c.blocks/hash (u.links/report-link ::m.c.blocks/hash u.links/ui-block-link)}
   ro/route            "blocks"
   ro/row-actions      [delete-action-button]
   ro/row-pk           m.c.blocks/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.c.blocks/admin-index
   ro/title            "Admin Core Blocks"})
