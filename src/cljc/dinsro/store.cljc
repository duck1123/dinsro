(ns dinsro.store
  (:require
   [taoensso.timbre :as timbre]))

(defprotocol Store
  "Backend for fetching data"
  (get-state [this] "Get State")
  (dispatch [this selector] "dispatch")
  (subscribe [this selector] "Subscribe")
  (reg-sub [this selector handler] "Reg sub"))
