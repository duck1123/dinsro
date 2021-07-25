(ns dinsro.accounts-page
  (:require-macros [latte.core :refer [describe it]]))

(def cy js/cy)

(describe "Admin Page"
  (it "should have an admin page"
    []
    (.. cy (visit "/accounts"))
    (.. cy (get ":nth-child(1) > :nth-child(5) > .button") (should "exist"))

    (.. cy (get ":nth-child(1) > :nth-child(3) > a")
        (then
         (fn [link]
           (let [text (.text link)]
             (.. cy (get link) click)
             (.. cy (get ".show-currency > :nth-child(1)") (should "have.text" text))))))))
