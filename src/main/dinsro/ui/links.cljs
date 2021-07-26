(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defsc AccountLink
  [_this {::m.accounts/keys [id name]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/id   nil
                   ::m.accounts/name ""}
   :query         [::m.accounts/id
                   ::m.accounts/name]}
  (let [path (str "/accounts/" id)]
    (dom/a {:href path} name)))

(def ui-account-link (comp/factory AccountLink {:keyfn ::m.accounts/id}))

(defsc CategoryLink
  [_this {::m.categories/keys [id name]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/name ""}
   :query         [::m.categories/id ::m.categories/name]}
  (let [path (str "/categories/" id)]
    (dom/a {:href path} name)))

(def ui-category-link (comp/factory CategoryLink {:keyfn ::m.categories/id}))

(defsc CurrencyLink
  [_this {::m.currencies/keys [id name]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/name ""}
   :query         [::m.currencies/name
                   ::m.currencies/id]}
  (let [path (str "/currencies/" id)]
    (dom/a {:href path} name)))

(def ui-currency-link (comp/factory CurrencyLink {:keyfn ::m.currencies/name}))

(defsc RateSourceLink
  [_this {::m.rate-sources/keys [id name]}]
  {:ident         ::m.rate-sources/id
   :initial-state {::m.rate-sources/id   nil
                   ::m.rate-sources/name ""}
   :query         [::m.rate-sources/id
                   ::m.rate-sources/name]}
  (let [path (str "/rate-sources/" id)]
    (dom/a {:href path} name)))

(def ui-rate-source-link (comp/factory RateSourceLink {:keyfn ::m.rate-sources/id}))

(defsc TransactionLink
  [_this {::m.transactions/keys [id description]}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/id          nil
                   ::m.transactions/description ""}
   :query         [::m.transactions/id
                   ::m.transactions/description]}
  (let [path (str "/transactions/" id)]
    (dom/a {:href path} description)))

(def ui-transaction-link (comp/factory TransactionLink {:keyfn ::m.transactions/id}))

(defsc UserLink
  [_this {::m.users/keys [id]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id nil}
   :query         [::m.users/id]}
  (let [path (str "/users/" id)]
    (dom/a {:href path} id)))

(def ui-user-link (comp/factory UserLink {:keyfn ::m.users/id}))
