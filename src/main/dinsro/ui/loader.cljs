(ns dinsro.ui.loader
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [lambdaisland.glogc :as log]))

(defn merge-pages
  [{:keys [data-tree] :as ctx} id-key page-map]
  (let [id (get data-tree id-key)]
    (log/info :merge-pages/starting {:id-key id-key :id id :page-map page-map :ctx ctx :data-tree data-tree})
    (let [states
          (->> page-map
               (map
                (fn [[key [page args]]]
                  (log/trace :merge-pages/processing-page {:key key :page page :args args})
                  (let [data    {id-key id :id id}
                        initial (comp/get-initial-state page (merge data args))
                        state   (merge initial data)]
                    (log/debug :merge-pages/process-page {:key key :page page :state state})
                    {key state})))
               (into {}))
          merged-state (merge data-tree states {:ui/page-merged true})]
      (log/info :merge-pages/finished {:merged-state merged-state})
      merged-state)))

(def skip-loaded true)

(defn page-loader
  "Returns a will-enter handler for a page"
  [key control-key app {id :id}]
  (let [id             (new-uuid id)
        ident          [key id]
        parent-control (comp/registry-key->class control-key)
        state          (-> (app/current-state app) (get-in ident))]
    (log/info :page-loader/starting {:key key :control-key control-key :id id :state state})
    (if (and skip-loaded (:ui/page-merged state))
      (do
        (log/debug :page-loader/routing-immediate {:ident ident})
        (dr/route-immediate ident))
      (do
        (log/debug :page-loader/deferring {:ident ident})
        (dr/route-deferred
         ident
         (fn []
           (log/trace :page-loader/routing {:key key :id id :parent-control parent-control})
           (df/load!
            app ident parent-control
            {:marker               :ui/selected-node
             :target               [:ui/selected-node]
             :post-mutation        `dr/target-ready
             :post-mutation-params {:target ident}})))))))

(defn subpage-loader
  "componentDidMount handler for SubPage components that load a report"
  [ident-key router-key Report this]
  (let [props    (comp/props this)
        parent-id (get-in props [[::dr/id router-key] ident-key])]
    (report/start-report! this Report {:route-params {ident-key parent-id}})))

(defn page-merger
  [k mappings]
  (log/trace :page-merger/starting {:k k :mappings mappings})
  (fn [ctx]
    (log/trace :page-merger/merging {:k k :mappings mappings :ctx ctx})
    (merge-pages ctx k mappings)))
