(ns dinsro.users-page
  (:require-macros [latte.core :refer [describe it]]))

(def cy js/cy)

(describe "Users Page"
  (it "should have an users page"
    []
    (.. cy (visit "/users"))
    ;; (.. cy (get ":nth-child(1) > :nth-child(5) > .button") (should "exist"))

    ;; (.. cy (get ":nth-child(1) > :nth-child(3) > a")
    ;;     (then
    ;;      (fn [link]
    ;;        (let [text (.text link)]
    ;;          (.. cy (get link) click)
    ;;          (.. cy (get ".show-currency > :nth-child(1)") (should "have.text" text))))))

    (.. cy (get "tr > :nth-child(1) > a") (should "have.text" "admin"))
    ))
