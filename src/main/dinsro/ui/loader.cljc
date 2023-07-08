(ns dinsro.ui.loader
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.navlinks :as mu.navlinks]
   [lambdaisland.glogc :as log]))

(def skip-loaded false)

(defn process-state
  [id-key id [key [component args]]]
  (log/trace :process-state/starting
    {:id-key    id-key
     :id        id
     :key       key
     :component component
     :args      args})
  (let [data    (if id {id-key id :id id} {})
        initial (comp/get-initial-state component (merge data args))
        state   (merge initial data)]
    (log/trace :process-state/finished
      {:key       key
       :component component
       :state     state})
    {key state}))

(defn subpage-loader
  "componentDidMount handler for SubPage components that load a report"
  [parent-model-key router-key Report this]
  (let [props    (comp/props this)
        parent-id (get-in props [[::dr/id router-key] parent-model-key])]
    (report/start-report! this Report {:route-params {parent-model-key parent-id}})))

(defn merge-pages
  "Handles the process of initializing a page with the parent key"
  [{:keys [data-tree] :as ctx} parent-ident-key mappings]
  (let [page-key  (get data-tree ::m.navlinks/id)
        record-id (when parent-ident-key (get data-tree parent-ident-key))]
    (log/trace :merge-pages/starting
      {:page-key         page-key
       :parent-ident-key parent-ident-key
       :record-id        record-id
       :mappings         mappings
       :ctx              ctx
       :data-tree        data-tree})
    (let [states       (->> mappings
                            (map (partial process-state parent-ident-key record-id))
                            (into {}))
          merged-state (merge data-tree states {:ui/page-merged true})]
      (log/trace :merge-pages/finished
        {:page-key         page-key
         :parent-ident-key parent-ident-key
         :record-id        record-id
         :merged-state     merged-state})
      merged-state)))

(defn page-merger
  "pre-merge handler for show pages"
  [parent-ident-key mappings]
  (fn [ctx]
    (let [merged (merge-pages ctx parent-ident-key mappings)]
      (log/trace :page-merger/finished
        {:parent-ident-key parent-ident-key
         :merged           merged
         :mappings         mappings
         :ctx              ctx})
      merged)))

(>defn page-loader
  "Returns a will-enter handler for a page"
  [page-id]
  [keyword? => fn?]
  (fn [app props]
    (log/debug :page-loader/starting {:page-id page-id :props props})
    (let [ident [::m.navlinks/id page-id]]
      (dr/route-deferred ident
        (fn []
          (let [data `[(dinsro.mutations.navlinks/target-ready {:page-id ~page-id})]]
            (comp/transact! app data)))))))

(>defn targeted-page-loader
  "Returns a will-enter handler for an id page. Reads the :id key from the route parameters

  * page-key - The navlinks key to the current page
  * model-key - The model id key for this record
  * control-key - A keyword naming the Show control"
  [page-id model-key control-key]
  [keyword? keyword? keyword? => fn?]
  (let [page-ident [::m.navlinks/id page-id]]
    (fn [app props]
      (if-let [id (get props :id)]
        (let [record-id      (when id (new-uuid id))
              parent-control (comp/registry-key->class control-key)
              current-state  (app/current-state app)
              state          (get-in current-state page-ident)]
          (log/trace :targeted-page-loader/starting
            {:page-id        page-id
             :record-id      record-id
             :model-key      model-key
             :current-state  current-state
             :parent-control parent-control
             :state          state})
          (if parent-control
            (if (and skip-loaded (:ui/page-merged state))
              (do
                (log/debug :targeted-page-loader/routing-immediate
                  {:page-id        page-id
                   :record-id      record-id
                   :model-key      model-key
                   :parent-control parent-control})
                (dr/route-immediate page-ident))
              (do
                (log/debug :targeted-page-loader/deferring
                  {:page-id        page-id
                   :record-id      record-id
                   :model-key      model-key
                   :parent-control parent-control})
                (dr/route-deferred page-ident
                  (fn []
                    (df/load!
                     app page-ident parent-control
                     {:params               {model-key record-id}
                      :post-mutation        `mu.navlinks/target-ready
                      :post-mutation-params {:model-key model-key
                                             :page-id   page-id
                                             :record-id record-id}})))))
            (throw (ex-info "Failed to determine parent control" {}))))
        (throw (ex-info "No id" {}))))))

(defn targeted-router-loader
  "will enter handler for a show page that contains a router"
  [page-id model-key control-key]
  (fn [app props]
    (let [{::app/keys [state-atom]} app
          {:keys [id]}              props
          record-id                 (new-uuid id)
          ident                     [::m.navlinks/id page-id]
          control                   (comp/registry-key->class control-key)]
      (log/info :targeted-router-loader/starting
        {:page-id    page-id
         :model-key  model-key
         :record-id  record-id
         :app        app
         :props      props
         :state-atom state-atom
         :page       (get-in @state-atom ident)})

      ;; Set the model key on the page
      (swap! state-atom assoc-in (conj ident model-key) record-id)

      (log/info :targeted-router-loader/merged
        {:page-id     page-id
         :control-key control-key
         :page        (get-in @state-atom ident)})
      (dr/route-deferred ident
        (fn []
          (df/load! app ident control
                    {:params               {model-key record-id}
                     :post-mutation        `mu.navlinks/routing-target-ready
                     :post-mutation-params {:model-key model-key
                                            :page-id   page-id
                                            :record-id record-id}}))))))

(defn targeted-subpage-loader
  "will enter handler for sub-pages of a targeted router"
  [page-id model-key control-key]
  (fn [app props]
    (let [{::app/keys [state-atom]} app
          {:keys [id]}              props
          record-id                 (new-uuid id)
          ident                     [::m.navlinks/id page-id]
          control                   (comp/registry-key->class control-key)]
      (log/debug :targeted-subpage-loader/starting
        {:page-id page-id
         :props props
         :record-id record-id})
      (swap! state-atom assoc-in [::m.navlinks/id page-id model-key] record-id)
      (dr/route-deferred ident
        (fn []
          (df/load! app ident control
                    {:params               {model-key record-id}
                     :post-mutation        `mu.navlinks/target-ready
                     :post-mutation-params {:model-key model-key
                                            :page-id   page-id
                                            :record-id record-id}}))))))
