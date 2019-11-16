(ns dinsro.view.login-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [dinsro.views.login :as v.login]
            [taoensso.timbre :as timbre]))

(deftest login-form
  (is (= nil (v.login/login-form))))

(comment
  (v.login/login-form)
  )
