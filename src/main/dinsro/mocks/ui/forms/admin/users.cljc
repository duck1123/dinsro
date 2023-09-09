(ns dinsro.mocks.ui.forms.admin.users
  (:require
   [com.fulcrologic.fulcro.algorithms.form-state :as fs]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.options.users :as o.users]
   [dinsro.specs :as ds]))

(defn get-state
  []
  {o.users/name      (ds/gen-key o.users/id)
   o.users/password  (ds/gen-key o.users/password)
   o.users/role      :account.role/admin
   ::fs/config
   {::fs/complete?      #{o.users/name o.users/role o.users/password}
    ::fs/fields         #{o.users/name o.users/role o.users/password}
    ::fs/id             [o.users/id (new-uuid "ce7df520-3ea6-4140-aced-2acd2d2023ff")]
    ::fs/pristine-state {}
    ::fs/subforms       {}}})
