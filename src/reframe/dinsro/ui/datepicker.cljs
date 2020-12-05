(ns dinsro.ui.datepicker
  (:require
   [reagent.core :as r]
   [reagent.dom :as rd]
   [taoensso.timbre :as timbre]))

(defn mount-component
  [comp]
  (let [opts (r/props comp)
        e (rd/dom-node comp)
        js-opts (clj->js (dissoc opts :on-select))
        instance (js/bulmaCalendar. e js-opts)]
    (when-let [on-select (:on-select opts)]
      (.on instance "select"
           (fn [datepicker]
             (let [value (.toISOString (js/Date. (.value (.-data datepicker))))]
               (on-select value)))))))

(defn update-component
  [_comp]
  #_(mount-component comp))

(defn datepicker-inner
  []
  (r/create-class
   {:component-did-mount mount-component
    :component-did-update update-component
    :reagent-render (fn [_] [:input.input])}))

(defn datepicker-outer
  [config]
  [datepicker-inner config])

(defn datepicker
  [props]
  (let [config (merge {:todayButton false
                       :minuteSteps 1
                       :type "datetime"
                       :validateLabel "OK"
                       :showClearButton false
                       :showHeader false}
                      props)]
    [datepicker-outer config]))
