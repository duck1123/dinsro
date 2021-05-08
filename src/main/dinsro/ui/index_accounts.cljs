(ns dinsro.ui.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexAccountLine
  [_this {::m.accounts/keys [currency id initial-value name user]}]
  {:initial-state {::m.accounts/currency      ""
                   ::m.accounts/id            1
                   ::m.accounts/initial-value 0
                   ::m.accounts/name          ""
                   ::m.accounts/user          ""}
   :query         [::m.accounts/currency
                   ::m.accounts/id
                   ::m.accounts/initial-value
                   ::m.accounts/name
                   ::m.accounts/user]}
  (dom/tr {}
    (dom/td name)
    (dom/td user)
    (dom/td currency)
    (dom/td initial-value)
    (dom/td (u.buttons/ui-delete-account-button {::m.accounts/id id}))))

(def ui-index-account-line
  (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this {::keys [accounts]}]
  {:initial-state {::accounts []}
   :query         [{::accounts (comp/get-query IndexAccountLine)}]}
  (dom/div {}
    (dom/table :.table
      (dom/thead {}
        (dom/tr {}
          (dom/th (tr [:name]))
          (dom/th (tr [:user-label]))
          (dom/th (tr [:currency-label]))
          (dom/th (tr [:initial-value-label]))
          (dom/th (tr [:buttons]))))
      (dom/tbody {}
        (map ui-index-account-line accounts)))))
