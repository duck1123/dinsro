(ns dinsro.ui.debug
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(def blacklisted-keys
  "Keys that will not be logged by log-props"
  #{:com.fulcrologic.fulcro.ui-state-machines/asm-id
    :com.fulcrologic.fulcro.application/active-remotes
    :com.fulcrologic.fulcro.ui-state-machines/ident->actor
    :com.fulcrologic.fulcro.ui-state-machines/state-machine-id
    :com.fulcrologic.fulcro.ui-state-machines/active-timers
    :com.fulcrologic.fulcro.ui-state-machines/actor->component-name
    :com.fulcrologic.fulcro.ui-state-machines/actor->ident})

(defn log-props
  "Display a map for debugging purposes"
  [props]
  (dom/dl :.ui.segment
    (->> (keys props)
         (filter (fn [k] (not (blacklisted-keys k))))
         (map
          (fn [k]
            ^{:key k}
            (comp/fragment
             (dom/dt {} (str k))
             (dom/dd {}
               (let [v (get props k)]
                 (if (map? v)
                   (log-props v)
                   (do
                     (str v)
                     (if (vector? v)
                       (dom/ul {}
                         (map
                          (fn [vi]
                            ^{:key (str k vi)}
                            (dom/li {} (log-props vi)))
                          v))
                       (dom/div :.ui.segment (str v)))))))))))))

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
