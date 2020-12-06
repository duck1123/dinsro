(ns dinsro.cards
  #?(:cljs
     (:require
      [devcards.core]
      [reagent.core :as r]))
  #?(:cljs
     (:require-macros
      [dinsro.cards])))

(defmacro defcard-rg
  [name & body]
  `(devcards.core/defcard ~name
     (reagent.core/as-element ((fn [] ~@body)))))

(defmacro deftest
  [name & body]
  `(devcards.core/deftest ~name ~@body))

(defmacro defcard
  [name & body]
  `(devcards.core/defcard ~name ~@body))

(defmacro assert-spec
  [spec value]
  (let [card-name (symbol (str value "-matches-" (name spec)))
        test-name (gensym (symbol (str card-name "-test")))]
    `(let [valid?# (clojure.spec.alpha/valid? ~spec ~value)]
       (when-not valid?#
         (devcards.core/defcard-rg ~card-name
           [:pre (expound.alpha/expound-str ~spec ~value)])
         (devcards.core/deftest ~test-name
           (cljs.test/is (clojure.spec.alpha/valid? ~spec ~value)))))))
