(ns dinsro.viewers)

(def file-link-viewer
  {:name :file-link-viewer/main
   :render-fn
   '(fn display-file-links
      [props]
      (v/html
       [:ul
        (map
         (fn [{:nextjournal/keys [value]}]
           (let [f (:nextjournal/value (second (:nextjournal/value (first (filter #(= (:nextjournal/value (first (:nextjournal/value %))) :f) value)))))
                 n (:nextjournal/value (second (:nextjournal/value (first (filter #(= (:nextjournal/value (first (:nextjournal/value %))) :n) value)))))]
             [:li [:a {:on-click #(v/clerk-eval `(clerk/show! ~f))} n]]))
         props)]))})
