(ns dinsro.test-utils)

(defmacro assert-spec
  [spec value]
  (let [card-name (symbol (str value "-matches-" (name spec)))
        test-name (gensym (symbol (str card-name "-test")))]
    `(let [valid?# (clojure.spec.alpha/valid? ~spec ~value)]
       (when-not valid?#
         #_(devcards.core/defcard-rg ~card-name
             [:pre (expound.alpha/expound-str ~spec ~value)])
         (nubank.workspaces.core/deftest ~test-name
           (cljs.test/is (clojure.spec.alpha/valid? ~spec ~value)))))))
