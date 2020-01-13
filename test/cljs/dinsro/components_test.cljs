(ns dinsro.components-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.components :as c]
   [devcards.core :as dc :refer-macros [defcard defcard-rg deftest]]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [taoensso.timbre :as timbre]))

(defcard title
  #_"**components**"
  (r/as-element [:h1.title "Components"]))

(defcard-rg checkbox-input
  (with-redefs [rf/subscribe (fn [x] (timbre/spy :info x))]
    (r/as-element
     [:div
      [:p "Foo"]
      #_[c/checkbox-input "foo" :foo]])))

#_(defcard-rg account-selector
  [c/account-selector "foo" :foo]
  )

(deftest account-selector
  (let [label "foo"
        field :foo]
    (is (vector? (c/account-selector label field))))
  )
