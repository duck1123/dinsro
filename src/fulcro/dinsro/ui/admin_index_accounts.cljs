(ns dinsro.ui.admin-index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [taoensso.timbre :as timbre]))

(defsc AdminIndexAccountLine
  [_this {::m.accounts/keys [id name user-id currency-id initial-value]}]
  {:initial-state {::m.accounts/currency-id   0
                   ::m.accounts/id            0
                   ::m.accounts/initial-value 0
                   ::m.accounts/name          ""
                   ::m.accounts/user-id       0}
   :query [::m.accounts/currency-id
           ::m.accounts/id
           ::m.accounts/initial-value
           ::m.accounts/name
           ::m.accounts/user-id]}
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
  [_this {::keys [accounts form toggle-button]}]
  {:initial-state {::accounts      []
                   ::form          {}
                   ::toggle-button {}}
   :query [{::accounts      (comp/get-query AdminIndexAccountLine)}
           {::form          (comp/get-query u.f.admin-create-account/AdminCreateAccountForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]}
  (bulma/box
   (dom/h1
    (tr [:index-accounts])
    (u.buttons/ui-show-form-button toggle-button))
   (u.f.admin-create-account/ui-admin-create-account-form form)
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
