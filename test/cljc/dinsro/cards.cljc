(ns dinsro.cards
  (:require
   [clojure.set :as set]
   [clojure.string :as string]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def headers (atom {}))

(defn card-link
  [[sym [_filters title]]]
  ^{:key (pr-str sym)}
  (let [href (str "devcards.html#!/"
                  (string/replace (pr-str sym) #"-" "_"))]
    [:li
     [:a {:href href}
      title]]))

(defn matches-filters?
  [current-filters [_ [candidate-filters _]]]
  (->> current-filters
       (map (fn [current-filter]
              (let [matches (map #(empty? (set/difference current-filter %))
                                 candidate-filters)]
                (filter identity matches))))
       (filter identity)
       (map seq)
       (filter identity)
       seq
       boolean))

(defn card-body
  [title current-filters]
  [:div
   [:h1.title title]
   [:p (pr-str current-filters)]
   (->> @headers
        sort
        (filter (partial matches-filters? current-filters))
        (map card-link)
        (into [:ul]))])

(defmacro header
  [sym title filters]
  `(do
     (let [old-headers# @headers]
       (compare-and-set! headers old-headers# (assoc old-headers# ~sym [~filters ~title]))
       (devcards.core/defcard-rg title
         (fn [] (dinsro.cards/card-body ~title ~filters))))))
