(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc AccountLink
  [_this {::m.accounts/keys [id name]}]
  {:ident ::m.accounts/id
   :initial-state {::m.accounts/id   0
                   ::m.accounts/name ""}
   :query [::m.accounts/id
           ::m.accounts/name]}
  (let [path (str "/accounts/" id)]
    (dom/a {:href path} name)))

(def ui-account-link (comp/factory AccountLink {:keyfn ::m.accounts/id}))

(defsc CategoryLink
  [_this {::m.categories/keys [id name]}]
  {:ident ::m.categories/id
   :initial-state {::m.categories/id   0
                   ::m.categories/name ""}
   :query [::m.categories/id ::m.categories/name]}
  (let [path (str "/categories/" id)]
    (dom/a {:href path} name)))

(def ui-category-link (comp/factory CategoryLink {:keyfn ::m.categories/id}))

(defsc CurrencyLink
  [_this {::m.currencies/keys [id name]}]
  {:ident ::m.currencies/id
   :initial-state {::m.currencies/id   0
                   ::m.currencies/name ""}
   :query [::m.currencies/id ::m.currencies/name]}
  (let [path (str "/currencies/" id)]
    (dom/a {:href path} name)))

(def ui-currency-link (comp/factory CurrencyLink {:keyfn ::m.currencies/id}))

(defsc RateSourceLink
  [_this {::m.rate-sources/keys [id name]}]
  {:ident ::m.rate-sources/id
   :initial-state {::m.rate-sources/id   0
                   ::m.rate-sources/name ""}
   :query [::m.rate-sources/id
           ::m.rate-sources/name]}
  (let [path (str "/rate-sources/" id)]
    (dom/a {:href path} name)))

(def ui-rate-source-link (comp/factory RateSourceLink {:keyfn ::m.rate-sources/id}))

(defsc UserLink
  [_this {::m.users/keys [id name]}]
  {:ident ::m.users/id
   :initial-state {::m.users/id   0
                   ::m.users/name ""}
   :query [::m.users/id ::m.users/name]}
  (let [path (str "/users/" id)]
    (dom/a {:href path} name)))

(def ui-user-link (comp/factory UserLink {:keyfn ::m.users/id}))
