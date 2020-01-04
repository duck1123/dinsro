(ns dinsro.views.setting-test
  (:require [cljs.test :refer-macros [is]]
            [devcards.core :refer-macros [defcard-rg deftest]]
            [dinsro.views.settings :as v.settings]
            [taoensso.timbre :as timbre]))

(declare page)
(deftest page
  (is (vector? (v.settings/page))))

(defcard-rg page
  [v.settings/page])
