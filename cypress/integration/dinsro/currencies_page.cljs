(ns dinsro.currencies-page
  (:require-macros [latte.core :refer [describe it]]))

(def cy js/cy)

(describe "Currencies Page"
  (it "should have an currencies page"
    []
    (.. cy (visit "/currencies"))
    ;; (.. cy (get ":nth-child(1) > :nth-child(5) > .button") (should "exist"))

    ;; (.. cy (get ":nth-child(1) > :nth-child(3) > a")
    ;;     (then
    ;;      (fn [link]
    ;;        (let [text (.text link)]
    ;;          (.. cy (get link) click)
    ;;          (.. cy (get ".show-currency > :nth-child(1)") (should "have.text" text))))))

    (.. cy (get "tbody > :nth-child(1) > :nth-child(1)") (should "have.text" "usd"))
    ))
