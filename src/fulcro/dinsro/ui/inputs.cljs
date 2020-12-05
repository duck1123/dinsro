(ns dinsro.ui.inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc TextInput
  [_this _props]
  (dom/div "text input"))

(def ui-text-input
  (comp/factory TextInput))

(defsc AccountSelector
  [_this {:keys [accounts]}]
  {:query [:accounts]
   :initial-state {:accounts []}}
  (dom/div
   :.select
   (dom/select
    (map (fn [{::m.users/keys [id name]}]
           ^{:key id}
           (dom/option {:value id} name))
         accounts))))

(def ui-account-selector (comp/factory AccountSelector))
