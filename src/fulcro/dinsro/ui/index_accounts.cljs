(ns dinsro.ui.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountLine
  [_this {:keys [name user currency initial-value]}]
  {:query [:name :user :currency :initial-value]
   :initial-state {:name "name"
                   :user "bob"
                   :currency "sats"
                   :initial-value 23}}

  (dom/tr
   (dom/td name)
   (dom/td user)
   (dom/td currency)
   (dom/td initial-value)
   (dom/td
    (ui-button {:content (tr [:delete])}))))

(def ui-index-account-line
  (comp/factory IndexAccountLine))

(defsc IndexAccounts
  [_this {:keys [accounts]}]
  {:query [:accounts]
   :initial-state {:accounts [{:name "foo"}]}}
  (dom/div
   (dom/table
    :.table
    (dom/thead
     (dom/tr
      (dom/th (tr [:name]))
      (dom/th (tr [:user-label]))
      (dom/th (tr [:currency-label]))
      (dom/th (tr [:initial-value-label]))
      (dom/th (tr [:buttons]))))
    (dom/tbody
     (map ui-index-account-line accounts)))))
