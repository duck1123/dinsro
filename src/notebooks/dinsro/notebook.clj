^{:nextjournal.clerk/visibility #{:code :hide}}
(ns dinsro.notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [lambdaisland.glogc :as log]
   [nextjournal.clerk :as clerk]))

;; # Dinsro Notebooks

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; # Namespaces

^{::clerk/viewer clerk/table ::clerk/visibility {:code :hide}}
(nu/x2)

^{::clerk/visibility {:code :hide}
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

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/x2)

^{::clerk/visibility {:code :hide :result :hide}}
(comment

  4

  (clerk/show! "src/notebooks/dinsro/index.md")
  (clerk/show! "src/notebooks/dinsro/notebook.clj")
  (clerk/show! "src/notebooks/dinsro/client/bitcoin_s_notebook.clj")

  nil)
