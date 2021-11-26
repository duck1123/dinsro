(ns dinsro.currencies-page
  (:require
   [dinsro.support :refer [cy handle-pathom]])
  (:require-macros [latte.core :refer [beforeEach describe it]]))

(describe "Currencies Page"
  (beforeEach
   []
   (.intercept cy #js {:method "POST" :url "/api"} handle-pathom))

  (it "should have an currencies page"
    []
    (.. cy (visit "/currencies"))
    (comment
      (.. cy (get ":nth-child(1) > :nth-child(5) > .button") (should "exist"))

      (.. cy (get ":nth-child(1) > :nth-child(3) > a")
          (then
           (fn [link]
             (let [text (.text link)]
               (.. cy (get link) click)
               (.. cy (get ".show-currency > :nth-child(1)") (should "have.text" text))))))

      (.. cy (get "tbody > :nth-child(1) > :nth-child(1)") (should "have.text" "usd")))
    ))
