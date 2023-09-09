(ns dinsro.mocks.ui.forms.admin.currencies
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs :as ds]))

(defn get-state
  []
  {:name "currencies"
   ::fs/config
   {::fs/complete?      #{}
    ::fs/fields         #{}
    ::fs/id             [:dinsro.model.transactions/id (ds/gen-key ::m.transactions/id)]
    ::fs/pristine-state {}
    ::fs/subforms       {}}})
