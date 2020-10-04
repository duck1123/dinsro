(ns dinsro.cards
  (:require
   [clojure.set :as set]
   [clojure.string :as string]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def headers (atom {}))

(defn card-body
  [title current-filters]
  [:div
   [:h1.title title " " (pr-str current-filters)]
   [:ul (map
         (fn [[sym [_filters title]]]
           ^{:key (pr-str sym)}
           [:li
            [:a {:href (str "devcards.html#!/"
                            (string/replace (pr-str sym) #"-" "_"))}
             title]
            ;; " " (pr-str filters)
            ])
         (->> @headers
              sort
              (filter
               (fn [[_ [candidate-filters _]]]
                 (->> current-filters
                      (map (fn [current-filter]
                             (let [matches (map #(empty? (set/difference current-filter %))
                                                candidate-filters)]
                               (filter identity matches))))
                      (filter identity)
                      (map seq)
                      (filter identity)
                      seq
                      boolean)))))]])

(defmacro header
  [sym title filters]
  `(do
     (let [old-headers# @headers]
       (compare-and-set! headers old-headers# (assoc old-headers# ~sym [~filters ~title]))
       (devcards.core/defcard-rg title
         (fn [] (dinsro.cards/card-body ~title ~filters))))))
