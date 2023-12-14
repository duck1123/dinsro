(ns dinsro.ui.debug
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.semantic-ui.collections.table.ui-table :refer [ui-table]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-body :refer [ui-table-body]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-cell :refer [ui-table-cell]]
   [com.fulcrologic.semantic-ui.collections.table.ui-table-row :refer [ui-table-row]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [lambdaisland.glogc :as log]))

(def blacklisted-keys
  "Keys that will not be logged by log-props"
  #{:com.fulcrologic.fulcro.ui-state-machines/asm-id
    :com.fulcrologic.fulcro.application/active-remotes
    :com.fulcrologic.fulcro.ui-state-machines/ident->actor
    :com.fulcrologic.fulcro.ui-state-machines/state-machine-id
    :com.fulcrologic.fulcro.ui-state-machines/active-timers
    :com.fulcrologic.fulcro.ui-state-machines/actor->component-name
    :com.fulcrologic.fulcro.ui-state-machines/actor->ident
    :route-factory
    :ui/router})

(declare log-props)
(declare log-list)

(defn log-list-item
  "Display a debug of a list item"
  [parent-keys item-index value]
  (log/debug :log-props-list-item/starting
    {:value       value
     :item-index item-index
     :parent-keys parent-keys})
  (let [item-keys (conj parent-keys item-index)
        item-label (string/join " => " item-keys)]
    (dom/li {:key item-label :title item-label}
      (if (or (string? value) (keyword? value) (uuid? value))
        (str value)
        (if (and (vector? value)
                 (or (not= (count value) 2)
                     (not (keyword? (first value)))))

          (log-list value item-keys)
          (if (map? value)
            (log-props value item-keys)
            (if (nil? value)
              (dom/span {:style {:color "red"}} "nil")
              (str value))))))))

(defn log-list
  "Display a debug of a list"
  ([items] (log-list items []))
  ([items parent-keys]
   (dom/ol {:start 0}
           (map-indexed (partial log-list-item parent-keys) items))))

(defn log-props-line
  "Display a debug of a map item"
  [props parent-keys k]
  (let [value      (get props k)
        item-keys  (conj parent-keys k)
        item-label (string/join " => " item-keys)]
    (log/debug :log-props-line/starting
      {:k k :value value :parent-keys parent-keys})
    (dom/div {:key item-label :title item-label}
      (if (some #(% value) [map? vector?])
        (comp/fragment
         (dom/dt {} (str k))
         (dom/dd {:style {:marginInlineStart "0"}}
           (if (map? value)
             (log-props value item-keys)
             (if (vector? value)
               (log-list value item-keys)
               (ui-segment {}
                 (str value))))))
        (comp/fragment
         (dom/div (str k " => " value)))))))

(defn log-props-table-line
  "Display a debug of a map item"
  [props parent-keys k]
  (let [value      (get props k)
        item-keys  (conj parent-keys k)
        item-label (string/join " => " item-keys)]
    (log/debug :log-props-line/starting
      {:k k :value value :parent-keys parent-keys})
    (ui-table-row {:key item-label :title item-label}
      (ui-table-cell {} (str k))
      (ui-table-cell {} (if (nil? value)
                          (dom/span {:style {:color "red"}} "nil")
                          (str value))))))

(defn log-props
  "Display a map for debugging purposes"
  ([props]
   (log-props props []))
  ([props parent-keys]
   (log/debug :log-props/starting {:props props})
   (let [ks (keys props)]
     (log/trace :log-props/ks {:ks ks})
     (let [valid-keys (remove blacklisted-keys ks)]
       (log/trace :log-props/validated {:valid-keys valid-keys})
       (let [complex?       (fn [k]
                              (let [value (k props)]
                                (log/trace :complex?/value {:k k :value value})
                                (or (map? value)
                                    (and (vector? value)
                                         (or (not= (count value) 2)
                                             (not (keyword? (first value))))))))
             sub-table-keys (filter complex? valid-keys)]
         (log/info :log-props/keyss {:sub-table-key sub-table-keys})
         (let [value-keys (remove complex? valid-keys)]
           (log/info :log-props/keysv {:sub-table-key sub-table-keys :value-keys value-keys})
           (ui-segment {}
             (when (seq value-keys)
               (let [single-value-keys (sort (remove vector? value-keys))
                     vector-value-keys (filter vector? value-keys)
                     all-value-keys    (concat single-value-keys vector-value-keys)]
                 (ui-table {:basic "very"}
                   (ui-table-body {}
                     (map (partial log-props-table-line props parent-keys) all-value-keys)))))
             (when (seq sub-table-keys)
               (let [single-table-keys (sort (remove vector? sub-table-keys))
                     vector-table-keys (filter vector? sub-table-keys)
                     all-table-keys    (concat single-table-keys vector-table-keys)]
                 (map (partial log-props-line props parent-keys) all-table-keys))))))))))

(declare ui-inner-prop-logger)

(defsc PropLineLogger
  [_this {:keys [key value]}]
  (comp/fragment
   (dom/dt {} (str key))
   (dom/dd {}
     (if (map? value)
       (ui-inner-prop-logger value)
       (if (vector? value)
         (str value)
         (str value))))))

(def ui-prop-line-logger (comp/factory PropLineLogger {:keyfn (comp str :key)}))

(defsc InnerPropLogger
  [_this props]
  (let [filtered-keys (filter (complement blacklisted-keys) (keys props))]
    (dom/dl {:style {:margin 0
                     :border "1px black solid"}}
      (map (fn [k] (ui-prop-line-logger {:key k :value (get props k)}))
           filtered-keys))))

(def ui-inner-prop-logger (comp/factory InnerPropLogger))

(defsc PropsLogger
  "Log properties of a map"
  [_this props]
  (log-props props))

(def ui-props-logger (comp/factory PropsLogger))

(defn load-error
  "Displays a notice that a component failed to load"
  ([] (load-error nil))
  ([props]
   (load-error props "record"))
  ([props label]
   (ui-segment {:color "red" :inverted true}
     (dom/h3 {} (str "Failed to load " label))
     (when props
       (log-props props)))))
