(ns dinsro.ui.datepicker
  (:require
   ["bulma-calendar" :as BulmaCalendar]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [reagent.core :as r]
   [reagent.dom :as rd]
   [taoensso.timbre :as log]))

(defn mount-component
  [comp]
  (let [opts     (r/props comp)
        e        (rd/dom-node comp)
        js-opts  (clj->js (dissoc opts :on-select))
        instance (BulmaCalendar. e js-opts)]
    (when-let [on-select (:on-select opts)]
      (.on instance "select"
           (fn [datepicker]
             (let [value (.toISOString (js/Date. (.value (.-data datepicker))))]
               (on-select value)))))))

(defsc Datepicker
  [_this _props]
  {:componentDidMount
   (fn [this]
     (if-let [e (js/ReactDOM.findDOMNode this)]
       (let [props    (comp/props this)
             js-opts  (clj->js (dissoc props :on-select))
             instance (BulmaCalendar. (.querySelector e "input") js-opts)]
         (when-let [on-select (:on-select props)]
           (.on instance "select"
                (fn [datepicker]
                  (let [value (.toISOString (js/Date. (.. datepicker -data value)))]
                    (on-select value))))))
       (log/warn "Datepicker element was nil")))
   :initial-state {}
   :query         []}
  (dom/div {}
    (dom/input :.input
      {:onSelect (fn [_] (log/info "on select"))
       :onChange (fn [_] (log/info "changed"))
       :value    ""})))

(def ui-datepicker (comp/factory Datepicker))
