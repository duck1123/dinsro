(ns dinsro.ui.debug
  (:require
   [clojure.string :as string]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
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
  (log/debug :log-props-list-line/starting
    {:value       value
     :item-index item-index
     :parent-keys parent-keys})
  (let [item-keys (conj parent-keys item-index)
        item-label (string/join " => " item-keys)]
    (dom/li {:key item-label :title item-label}
      (if (or (string? value) (keyword? value) (uuid? value))
        (str value)
        (if (vector? value)
          (log-list value item-keys)
          (log-props value item-keys))))))

(defn log-list
  "Display a debug of a list"
  [items parent-keys]
  (dom/ul {}
    (map-indexed (partial log-list-item parent-keys) items)))

(defn log-props-line
  "Display a debug of a map item"
  [props parent-keys k]
  (let [value      (get props k)
        item-keys  (conj parent-keys k)
        item-label (string/join " => " item-keys)]
    (log/debug :log-props-line/starting
      {:k k :value value :parent-keys parent-keys})
    (dom/div {:key item-label :title item-label}
      (dom/dt {} (str k))
      (dom/dd {:style {:marginInlineStart "0"}}
        (if (map? value)
          (log-props value item-keys)
          (if (vector? value)
            (log-list value item-keys)
            (ui-segment {}
              (str value))))))))

(defn log-props
  "Display a map for debugging purposes"
  ([props]
   (log-props props []))
  ([props parent-keys]
   (log/debug :log-props/starting {:props props})
   (dom/dl :.log-props
     (ui-segment {}
       (->> (keys props)
            (filter (fn [k] (not (blacklisted-keys k))))
            (map (partial log-props-line props parent-keys)))))))

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
  (dom/div :.ui.segment
    (ui-inner-prop-logger props)))

(def ui-props-logger (comp/factory PropsLogger))
