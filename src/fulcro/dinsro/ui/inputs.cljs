(ns dinsro.ui.inputs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc TextInput
  [_this {:keys [label value]} {:keys [onChange]}]
  {:query [:label :value]
   :initial-state
   (fn [{:keys [label value]
         :or {label ""
              value ""}}]
     {:label label
      :value value})}
  (dom/div
   (dom/label :.label label)
   (dom/input
    :.input
    {:type :text
     :value value
     :onChange (fn [e] (onChange e))})))

(def ui-text-input
  (comp/computed-factory TextInput))

(defsc NumberInput
  [_this {:keys [label value]} {:keys [onChange]}]
  {:initial-state
   (fn [{:keys [label value]
         :or {label ""
              value ""}}]
     {:label label
      :value value})
   :query [:label :value]}
  (dom/div
   (dom/label :.label label)
   (dom/input
    :.input
    {:type :number
     :value value
     :onChange onChange})))

(def ui-number-input
  (comp/computed-factory NumberInput))

(defsc AccountSelector
  [_this {:keys [accounts]}]
  {:initial-state {:accounts []}
   :query [:accounts]}
  (dom/div
   :.select
   (dom/select
    (map (fn [{::m.accounts/keys [id name]}]
           ^{:key id}
           (dom/option {:value id} name))
         accounts))))

(def ui-account-selector (comp/factory AccountSelector))

(defsc CurrencySelector
  [_this {:keys [currencies]}]
  {:initial-state {:currencies []}
   :query [:currencies]}
  (dom/div
   :.select
   (dom/select
    (map (fn [{::m.currencies/keys [id name]}]
           ^{:key id}
           (dom/option {:value id} name))
         currencies))))

(def ui-currency-selector (comp/factory CurrencySelector))

(defsc UserSelector
  [_this {:keys [users]}]
  {:initial-state {:users []}
   :query [:users]}
  (dom/div
   :.select
   (dom/select
    (map (fn [{::m.users/keys [id name]}]
           ^{:key id}
           (dom/option {:value id} name))
         users))))

(def ui-user-selector (comp/factory UserSelector))

(defsc PrimaryButton
  [_this _props]
  {:initial-state {}
   :query []}
  (dom/button :.button "submit"))

(def ui-primary-button (comp/factory PrimaryButton))
