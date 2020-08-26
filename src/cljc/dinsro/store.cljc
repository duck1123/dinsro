(ns dinsro.store
  (:require
   [taoensso.timbre :as timbre]))

(defprotocol Store
  "Backend for fetching data"
  (get-state [this] "Get State")
  (dispatch [this a] "dispatch")
  (subscribe [this a] "Subscribe"))
