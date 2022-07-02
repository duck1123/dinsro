^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebooks
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]
   [lambdaisland.glogc :as log]))

;; # Dinsro Notebooks

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; # Namespaces

^{::clerk/viewer clerk/table ::clerk/visibility :hide}
(nu/x2)

^{::clerk/visibility :hide
  ::clerk/viewer
  {:render-fn
   '(fn display-file-links
      [props]
      (v/html
       [:ul
        (map
         (fn [{pair-values :nextjournal/value :nextjournal/keys [value] :as row}]
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
         props)]))}}
(nu/x2)

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/x2)

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(comment

  4

  (clerk/show! "src/notebooks/dinsro/index.md")
  (clerk/show! "src/notebooks/dinsro/notebook.clj")
  (clerk/show! "src/notebooks/dinsro/client/bitcoin_s_notebook.clj")

  nil)
