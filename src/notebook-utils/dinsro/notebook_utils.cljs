(ns dinsro.notebook-utils
  (:require [nextjournal.clerk.viewer :as v]))

(fn display-file-links
  [props]
  (v/html
   [:ul
    (map
     (fn [{:nextjournal/keys [value]}]
       (let [f (:nextjournal/value (second (:nextjournal/value (first (filter #(= (:nextjournal/value (first (:nextjournal/value %))) :f) value)))))
             n (:nextjournal/value (second (:nextjournal/value (first (filter #(= (:nextjournal/value (first (:nextjournal/value %))) :n) value)))))]
         [:li {:style {:border "1px green solid"}}
          [:a {:on-click
               (fn [e]
                 (js/console.log (str "Click: " f))
                 (v/clerk-eval `(do
                                  (log/info :clicked {})
                                  (clerk/show! ~f))))}
           n]]))
     props)]))
