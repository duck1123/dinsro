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
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [lambdaisland.glogc :as log]))

(def skip-loaded false)

(defn process-state
  "Creates the initial state for component passing the id and args and returns a map"
  [model-key id [state-key [component args]]]
  (log/trace :process-state/starting
    {:model-key model-key
     :id        id
     :state-key state-key
     :component component
     :args      args})
  (let [data         (if id {model-key id :id id} {})
        updated-data (merge data args)
        initial      (comp/get-initial-state component updated-data)
        state        (merge initial data)]
    (log/debug :process-state/finished
      {:state-key state-key
       :id        id
       :model-key model-key
       :component component
       :state     state})
    {state-key state}))

(defn subpage-loader
  "componentDidMount handler for SubPage components that load a report"
  [parent-model-key router-key Report this]
  (let [props     (comp/props this)
        parent-id (get-in props [[::dr/id router-key] parent-model-key])]
    (log/info :subpage-loader/starting
      {:parent-id        parent-id
       :parent-model-key parent-model-key
       :props            props})
    (report/start-report! this Report {:route-params {parent-model-key parent-id}})))

(defn merge-pages
  "Handles the process of initializing a page with the parent key"
  [{:keys [data-tree] :as ctx} parent-model-key mappings]
  (let [page-key  (get data-tree ::m.navlinks/id)
        record-id (when parent-model-key (get data-tree parent-model-key))]
    (log/trace :merge-pages/starting
      {:page-key         page-key
       :parent-model-key parent-model-key
       :record-id        record-id
       :mappings         mappings
       :ctx              ctx
       :data-tree        data-tree})
    (let [states       (->> mappings
                            (map (partial process-state parent-model-key record-id))
                            (into {}))
          merged-state (merge data-tree states {:ui/page-merged true})]
      (log/trace :merge-pages/finished
        {:page-key         page-key
         :parent-model-key parent-model-key
         :record-id        record-id
         :merged-state     merged-state})
      merged-state)))

(defn page-merger
  "pre-merge handler for show pages"
  [parent-model-key mappings]
  (fn [ctx]
    (let [merged (merge-pages ctx parent-model-key mappings)]
      (log/trace :page-merger/finished
        {:parent-model-key parent-model-key
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
        (let [{::app/keys [state-atom]} app
              record-id      (when id (new-uuid id))
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
            (do
              ;; Set the bmodel key on the page
              (swap! state-atom assoc-in (conj page-ident model-key) record-id)

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
                                               :record-id record-id}}))))))
            (throw (ex-info "Failed to determine parent control" {}))))
        (throw (ex-info "No id" {}))))))

(>defn targeted-router-loader
  "will enter handler for a show page that contains a router"
  [page-id model-key control-key]
  [keyword? keyword? keyword? => any?]
  (fn [app props]
    (let [{::app/keys [state-atom]} app
          {:keys [id]}              props
          record-id                 (new-uuid id)
          page-ident                [::m.navlinks/id page-id]
          control                   (comp/registry-key->class control-key)
          model-ident               (conj page-ident model-key)]
      (log/debug :targeted-router-loader/starting
        {:page-id     page-id
         :model-key   model-key
         :record-id   record-id
         :props       props
         :state-atom  state-atom
         :page        (get-in @state-atom page-ident)})

      ;; Set the model key on the page
      (swap! state-atom assoc-in model-ident record-id)

      (log/debug :targeted-router-loader/merged
        {:page-id     page-id
         :control-key control-key
         :page        (get-in @state-atom page-ident)})
      (dr/route-deferred page-ident
        (fn []
          (df/load! app page-ident control
                    {:params               {model-key record-id}
                     :post-mutation        `mu.navlinks/routing-target-ready
                     :post-mutation-params {:model-key model-key
                                            :page-id   page-id
                                            :record-id record-id}}))))))

(>defn targeted-subpage-loader
  "will enter handler for sub-pages of a targeted router"
  [page-id model-key control-key]
  [keyword? keyword? keyword? => any?]
  (log/trace :target-subpage-loader/initializing
    {:page-id     page-id
     :model-key   model-key
     :control-key control-key})
  (fn [app props]
    (let [{::app/keys [data-tree
                       state-atom]} app
          {:keys [id]}              props
          record-id                 (new-uuid id)
          page-ident                [::m.navlinks/id page-id]
          control                   (comp/registry-key->class control-key)
          model-ident               (conj page-ident model-key)]
      (log/debug :targeted-subpage-loader/starting
        {:page-id   page-id
         :props     props
         :record-id record-id
         :model-key model-key})

      ;; Set the model key on the page
      (swap! state-atom assoc-in model-ident record-id)

      (log/debug :targeted-subpage-loader/merged {:data-tree data-tree
                                                  :state-atom state-atom})

      (dr/route-deferred page-ident
        (fn []
          (df/load! app page-ident control
                    {:params               {model-key record-id}
                     :post-mutation        `mu.navlinks/target-ready
                     :post-mutation-params {:model-key model-key
                                            :page-id   page-id
                                            :record-id record-id}}))))))

(>defn show-page
  [props model-key ui-show]
  [any? keyword? fn? => any?]
  (log/info :ShowPage/starting {:model-key model-key :props props})
  (let [target  (o.navlinks/target props)
        page-id (o.navlinks/id props)]
    (if (model-key props)
      (if target
        (ui-show target)
        (u.debug/load-error props (str page-id " target")))
      (u.debug/load-error props (str page-id " page")))))
