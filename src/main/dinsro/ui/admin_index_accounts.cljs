(ns dinsro.ui.admin-index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.admin-create-account :as u.f.admin-create-account]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc AdminIndexAccountLine
  [_this {::m.accounts/keys [currency id initial-value link user]}]
  {:initial-state {::m.accounts/currency      {}
                   ::m.accounts/id            0
                   ::m.accounts/initial-value 0
                   ::m.accounts/link          {}
                   ::m.accounts/user          {}}
   :query [{::m.accounts/currency     (comp/get-query u.links/CurrencyLink)}
           {::m.accounts/link         (comp/get-query u.links/AccountLink)}
           ::m.accounts/id
           ::m.accounts/initial-value
           {::m.accounts/user         (comp/get-query u.links/UserLink)}]}
  (dom/tr
   (dom/td {} id)
   (dom/td {} (u.links/ui-account-link (first link)))
   (dom/td {} (u.links/ui-user-link user))
   (dom/td {} (u.links/ui-currency-link (first currency)))
   (dom/td {} initial-value)
   (dom/td {} (u.buttons/ui-delete-account-button {::m.accounts/id id}))))

(def ui-admin-index-account-line (comp/factory AdminIndexAccountLine {:keyfn ::m.accounts/id}))

(defsc AdminIndexAccounts
  [this {::keys [accounts form toggle-button]}]
  {:componentDidMount
   (fn [this]
     (timbre/info "did mount")
     (uism/begin! this machines/hideable form-toggle-sm {:actor/navbar AdminIndexAccounts}))
   :ident (fn [_] [:component/id ::AdminIndexAccounts])
   :initial-state {::accounts      []
                   ::form          {:form-button/id form-toggle-sm}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query [{::accounts      (comp/get-query AdminIndexAccountLine)}
           {::form          (comp/get-query u.f.admin-create-account/AdminCreateAccountForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2
      :.title.is-2
      (tr [:accounts])
      (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.admin-create-account/ui-admin-create-account-form form))
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
         (map ui-admin-index-account-line accounts)))))))

(def ui-section (comp/factory AdminIndexAccounts))
