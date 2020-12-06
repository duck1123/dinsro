(ns dinsro.ui.admin-index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexAccountLine
  [_this {::m.accounts/keys [id name user-id currency-id initial-value]}]
  {:query [::m.accounts/id
           ::m.accounts/name
           ::m.accounts/user-id
           ::m.accounts/currency-id
           ::m.accounts/initial-value]
   :initial-state {::m.accounts/id 0
                   ::m.accounts/name ""
                   ::m.accounts/user-id 0
                   ::m.accounts/currency-id 0
                   ::m.accounts/initial-value 0}}
  (dom/tr
   (dom/td id)
   (dom/td name)
   (dom/td user-id)
   (dom/td currency-id)
   (dom/td initial-value)
   (dom/td
    (dom/button :.button.is-danger "Delete"))))

(def ui-admin-index-account-line (comp/factory AdminIndexAccountLine {:keyfn ::m.accounts/id}))

(defsc AdminIndexAccounts
  [_this {:keys [accounts show-button form-data]}]
  {:initial-state {:accounts [{:m.accounts/name "foo"}]
                   :form-data {}
                   :show-button {}}
   :query [:accounts
           {:show-button (comp/get-query u.buttons/ShowFormButton)}
           {:form-data (comp/get-query u.f.admin-create-account/AdminCreateAccountForm)}]}
  (dom/div
   :.box
   (dom/h1 (tr [:index-accounts])
           (u.buttons/ui-show-form-button show-button))
   (u.f.admin-create-account/ui-admin-create-account-form form-data)
   (dom/hr)
   (if (empty? accounts)
     (dom/div (tr [:no-accounts]))
     (dom/table
      :.table
      (dom/thead
       (dom/tr
        (dom/th "Id")
        (dom/th (tr [:name]))
        (dom/th (tr [:user-label]))
        (dom/th (tr [:currency-label]))
        (dom/th (tr [:initial-value-label]))
        (dom/th (tr [:buttons]))))
      (dom/tbody
       (map ui-admin-index-account-line accounts))))))

(def ui-section (comp/factory AdminIndexAccounts))
