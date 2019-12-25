(ns dinsro.view.login-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [devcards.core :refer-macros [defcard-rg deftest]]
            [dinsro.views.login :as v.login]
            [taoensso.timbre :as timbre]))

(defcard-rg form
  ;; "**Documentation**"
  (fn [name] [:p name])
  {:name "foo"})


(deftest login-form
  (is (= nil (v.login/page ""))))

(comment
  ;; (v.login/login-form)
  )
