(ns dinsro.components.forms.add-user-transaction-test
  (:require [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.components.forms.create-category :as c.f.create-category]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.events.users :as e.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.events.forms.add-user-transaction :as s.e.f.add-user-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

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
