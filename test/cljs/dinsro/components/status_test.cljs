(ns dinsro.components.status-test
  (:require [day8.re-frame.http-fx]
            [devcards.core :refer-macros [defcard-rg]]
            [taoensso.timbre :as timbre]))

(defcard-rg status
  "**Documentation**"
  (fn [name] [:p name])
  {:name "foo"})
