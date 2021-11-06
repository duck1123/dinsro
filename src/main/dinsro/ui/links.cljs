(ns dinsro.ui.links
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.routing :as rroute]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defsc AccountLink
  [this {::m.accounts/keys [id name]}]
  {:ident         ::m.accounts/id
   :initial-state {::m.accounts/id   nil
                   ::m.accounts/name ""}
   :query         [::m.accounts/id
                   ::m.accounts/name]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.accounts/AccountForm)]
              (rroute/route-to! this component  {::m.accounts/id id})))}
    name))

(def ui-account-link (comp/factory AccountLink {:keyfn ::m.accounts/id}))

(form/defsc-form AccountLinkForm
  [_this _props]
  {fo/id         m.accounts/id
   fo/attributes [m.accounts/name]})

(defsc CategoryLink
  [this {::m.categories/keys [id name]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/name ""}
   :query         [::m.categories/id ::m.categories/name]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.categories/CategoryForm)]
              (rroute/route-to! this component  {::m.categories/id id})))}
    name))

(def ui-category-link (comp/factory CategoryLink {:keyfn ::m.categories/id}))

(form/defsc-form CategoryLinkForm
  [_this _props]
  {fo/id         m.categories/id
   fo/attributes [m.categories/name]})

(defsc CurrencyLink
  [this {::m.currencies/keys [id name]}]
  {:ident         ::m.currencies/id
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/code ""
                   ::m.currencies/name ""}
   :query         [::m.currencies/name
                   ::m.currencies/code
                   ::m.currencies/id]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.currencies/CurrencyForm)]
              (form/view! this component id)))}
    name))

(def ui-currency-link (comp/factory CurrencyLink {:keyfn ::m.currencies/name}))

(form/defsc-form CurrencyLinkForm [this {::m.currencies/keys [id name]}]
  {fo/id         m.currencies/id
   fo/attributes [m.currencies/name]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.currencies/CurrencyForm)]
              (rroute/route-to! this component  {::m.currencies/id id})))}
    name))

(defsc NodeLink
  [this {::m.ln-nodes/keys [id name]}]
  {:ident         ::m.ln-nodes/id
   :initial-state (fn [_props]
                    {::m.ln-nodes/id   nil
                     ::m.ln-nodes/name ""})
   :query         (fn [_props]
                    [::m.ln-nodes/id ::m.ln-nodes/name])}
  (dom/a {:onClick (fn [_e] (let [component (comp/registry-key->class :dinsro.ui.ln-nodes/LightningNodeForm)]
                              (form/view! this component id)))} name))

(def ui-node-link (comp/factory NodeLink {:keyfn ::m.ln-nodes/id}))

(form/defsc-form NodeLinkForm [_this _props]
  {fo/id         m.ln-nodes/id
   fo/attributes [m.ln-nodes/name]})

(defsc RateSourceLink
  [this {::m.rate-sources/keys [id name]}]
  {:ident         ::m.rate-sources/id
   :initial-state {::m.rate-sources/id   nil
                   ::m.rate-sources/name ""}
   :query         [::m.rate-sources/id
                   ::m.rate-sources/name]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.rate-sources/RateSourceForm)]
              (rroute/route-to! this component  {::m.rate-sources/id id})))}
    name))

(def ui-rate-source-link (comp/factory RateSourceLink {:keyfn ::m.rate-sources/id}))

(form/defsc-form RateSourceLinkForm [_this _props]
  {fo/id         m.rate-sources/id
   fo/attributes [m.rate-sources/name]})

(defsc TransactionLink
  [this {::m.transactions/keys [id description]}]
  {:ident         ::m.transactions/id
   :initial-state {::m.transactions/id          nil
                   ::m.transactions/description ""}
   :query         [::m.transactions/id
                   ::m.transactions/description]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.transactions/TransactionForm)]
              (rroute/route-to! this component  {::m.transactions/id id})))}
    description))

(def ui-transaction-link (comp/factory TransactionLink {:keyfn ::m.transactions/id}))

(form/defsc-form TransactionLinkForm [_this _props]
  {fo/id         m.transactions/id
   fo/attributes [m.transactions/id]})

(defsc UserLink
  [this {::m.users/keys [id name]}]
  {:ident         ::m.users/id
   :initial-state {::m.users/id   nil
                   ::m.users/name ""}
   :query         [::m.users/id ::m.users/name]}
  (dom/a {:onClick
          (fn [_e]
            (let [component (comp/registry-key->class :dinsro.ui.users/UserForm)]
              (form/view! this component id)))}
    name))

(def ui-user-link (comp/factory UserLink {:keyfn ::m.users/id}))

(form/defsc-form UserLinkForm [_this _props]
  {fo/id         m.users/id
   fo/attributes [m.users/name]})
