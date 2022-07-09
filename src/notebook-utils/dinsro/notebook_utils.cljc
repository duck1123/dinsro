(ns dinsro.notebook-utils
  (:require
   [clojure.tools.namespace.find :as n.find]
   [clojure.tools.namespace.file :as n.file]
   [clojure.tools.namespace.parse :as n.parse]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v])
  (:import java.io.File))

(defn display
  [o]
  (v/html [:pre [:code (clerk/code o)]]))

(defn display2
  [o]
  (clerk/code o))

^{::clerk/viewer     clerk/hide-result
  ::clerk/visibility :hide}
(def text-input
  {:pred         ::clerk/var-from-def
   :fetch-fn     (fn [_ x] x)
   :transform-fn (fn [{::clerk/keys [var-from-def]}]
                   {:var-name (symbol var-from-def) :value @@var-from-def})
   :render-fn
   '(fn [{:keys [var-name value]}]
      (v/html
       [:div.my-1.relative
        [:input {:type        :text
                 :autocorrect "off"
                 :spellcheck  "false"
                 :value       value
                 :class       "px-3 py-2 relative bg-white bg-white rounded text-base font-sans border border-slate-200 shadow-inner outline-none focus:outline-none focus:ring w-full"

                 :on-input #(v/clerk-eval `(reset! ~var-name ~(.. % -target -value)))}]
        [:button.absolute.right-2.text-xl.cursor-pointer
         {:class "top-1/2 -translate-y-1/2"
          :on-click
          #(v/clerk-eval `(reset! ~var-name ~(str/join "." (drop-last (str/split value #"\.")))))} "⏮"]]))})

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(defn x
  []
  (n.find/find-ns-decls [(File. "src/notebooks")]))

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(defn x2
  []
  (map
   (fn [f]
     (let [n (n.parse/name-from-ns-decl (n.file/read-file-ns-decl f))]
       {:n n :f (str f)}))
   (n.find/find-sources-in-dir (File. "src/notebooks"))))

(defn display-file-links
  []
  (clerk/with-viewers
    (clerk/add-viewers [:file-link-viewer/main])
    (x2)))

#_(fn display-file-links
    [props]
    (v/html
     [:ul
      (map
       (fn [{:nextjournal/keys [value]}]
         (let [f (:nextjournal/value (second (:nextjournal/value (first (filter #(= (:nextjournal/value (first (:nextjournal/value %))) :f) value)))))
               n (:nextjournal/value (second (:nextjournal/value (first (filter #(= (:nextjournal/value (first (:nextjournal/value %))) :n) value)))))]
           [:li {:style {:border "1px green solid"}}
            [:a {:on-click
                 (fn []
                   #?(:clj
                      (comment f)
                      :cljs
                      (js/console.log (str "Click: " f))
                      (v/clerk-eval `(do
                                       (log/info :clicked {})
                                       (clerk/show! ~f)))))}
             n]]))
       props)]))
