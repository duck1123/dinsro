(ns dinsro.views.setting-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.views.settings :as v.settings]
   [taoensso.timbre :as timbre]))

(deftest page
  (is (vector? (v.settings/page))))

(defcard-rg page-card
  [v.settings/page])
