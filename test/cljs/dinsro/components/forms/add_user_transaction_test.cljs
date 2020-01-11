(ns dinsro.components.forms.add-user-transaction-test
  (:require [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.translations :refer [tr]]))

(defcard
  (ds/gen-key ::e.f.add-user-transaction/form-data))

(defcard-rg form
  ;; "Create a transaction when the user is already provided"
  [c.f.add-user-transaction/form-shown])
