(ns dinsro.components.forms.add-user-transaction-test
  (:require [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
            [dinsro.spec :as ds]
            [dinsro.translations :refer [tr]]))

(defcard sample-form-data
  (ds/gen-key ::e.f.add-user-transaction/form-data)
  )

(defcard-rg form
  "**Add User Transaction**"
  ;; "Create a transaction when the user is already provided"
  (fn [name]
    [c.debug/debug-box name]
    [c.f.add-user-transaction/form-shown {}])
  {:name "foo"})
