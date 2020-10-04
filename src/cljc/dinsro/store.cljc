(ns dinsro.store
  (:require
   [taoensso.timbre :as timbre]))

(defprotocol Store
  "Backend for fetching data"
  (get-state [this] "Get State")
  (dispatch [this selector] "dispatch")
  (path-for [store handler] [store handler params] "")
  (subscribe [this selector] "Subscribe")
  (reg-basic-sub [this key] [this name key] [this name key sort-fn] "reg basic sub")
  (reg-event-fx [this id handler] [this id interceptors handler] "reg event fx")
  (reg-set-event [store key] [store event-kw kw] "reg set event")
  (reg-sub [this selector handler] "Reg sub"))
