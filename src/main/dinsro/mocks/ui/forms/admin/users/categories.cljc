(ns dinsro.mocks.ui.forms.admin.users.categories
  (:require
   [dinsro.joins.categories :as j.categories]
   [dinsro.model.categories :as m.categories]
   [dinsro.specs :as ds]))

(defn make-body-item
  []
  {::m.categories/name              (ds/gen-key ::m.categories/name)
   ::m.categories/id                (ds/gen-key ::m.categories/id)
   ::j.categories/transaction-count (ds/gen-key ::j.categories/transaction-count)})
