(ns dinsro.ui.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [taoensso.timbre :as log]))

(defsc IndexAccountLine
  [_this {::m.accounts/keys [currency id initial-value name user]}]
  {:initial-state {::m.accounts/currency      {}
                   ::m.accounts/id            nil
                   ::m.accounts/initial-value 0
                   ::m.accounts/name          ""
                   ::m.accounts/user          {}}
   :query         [{::m.accounts/currency (comp/get-query u.links/CurrencyLink)}
                   ::m.accounts/id
                   ::m.accounts/initial-value
                   ::m.accounts/name
                   {::m.accounts/user (comp/get-query u.links/UserLink)}]}
  (dom/tr {}
    (dom/td name)
    (dom/td (u.links/ui-user-link user))
    (dom/td (u.links/ui-currency-link currency))
    (dom/td initial-value)
    (dom/td (u.buttons/ui-delete-account-button {::m.accounts/id id}))))

(def ui-index-account-line
  (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this {::keys [accounts]}]
  {:initial-state {::accounts []}
   :query         [{::accounts (comp/get-query IndexAccountLine)}]}
  (dom/div {}
    (dom/table :.ui.table
      (dom/thead {}
        (dom/tr {}
          (dom/th (tr [:name]))
          (dom/th (tr [:user-label]))
          (dom/th (tr [:currency-label]))
          (dom/th (tr [:initial-value-label]))
          (dom/th (tr [:buttons]))))
      (dom/tbody {}
        (map ui-index-account-line accounts)))))

(defsc IndexAccountsPage
  [_this {:session/keys [current-user-ref]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :session/current-user-ref
               u.user-accounts/UserAccounts
               {:target [:page/id
                         ::page
                         :session/current-user-ref]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {:session/current-user-ref {}}
   :query         [:page/id
                   {:session/current-user-ref (comp/get-query u.user-accounts/UserAccounts)}]
   :route-segment ["accounts"]}
  (when current-user-ref
    (bulma/page
     {:className "index-accounts-page"}
     (u.user-accounts/ui-user-accounts current-user-ref))))

(def ui-page (comp/factory IndexAccountsPage))
