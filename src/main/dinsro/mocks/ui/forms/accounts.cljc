(ns dinsro.mocks.ui.forms.accounts
  (:require
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.options.accounts :as o.accounts]
   [dinsro.options.currencies :as o.currencies]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../../ui/forms/accounts.cljc]]
;; [[../../../../../test/dinsro/ui/forms/accounts_test.cljs]]

(defn CurrencyListItem-data
  [_a]
  {o.currencies/id   (ds/gen-key o.currencies/id)
   o.currencies/name (ds/gen-key o.currencies/name)})

(defn NewForm-data
  [a]
  (log/info :NewForm-data/starting {:a a})
  {o.accounts/id            (ds/gen-key o.accounts/id)
   o.accounts/initial-value (ds/gen-key o.accounts/initial-value)
   o.accounts/name          (ds/gen-key o.accounts/name)
   o.accounts/currency      {o.currencies/id   (ds/gen-key o.currencies/id)
                             o.currencies/name (ds/gen-key o.currencies/name)}})

(defn InlineForm-form-data
  [_a]
  {:component/id            :Inline
   o.accounts/initial-value (ds/gen-key o.accounts/initial-value)
   o.accounts/name          (ds/gen-key o.accounts/name)
   o.accounts/currency      (ds/gen-key o.accounts/currency)})

(defn InlineForm-component-data
  [_a]
  (let [currency-id (ds/gen-key o.accounts/currency)]
    {:component/id             :NewForm
     ::j.currencies/flat-index [{o.currencies/id   currency-id
                                 o.currencies/name (ds/gen-key o.currencies/name)}]
     o.accounts/initial-value  (ds/gen-key o.accounts/initial-value)
     o.accounts/name           (ds/gen-key o.accounts/name)
     :ui/currency-id           currency-id
     :ui/currencies-loaded?    true}))
