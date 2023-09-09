(ns dinsro.mocks.ui.forms.categories
  (:require
   [dinsro.options.categories :as o.categories]
   [dinsro.options.users :as o.users]
   [dinsro.specs :as ds]))

(defn NewForm-data
  [_a]
  {o.categories/id (ds/gen-key o.categories/id)
   o.categories/name (ds/gen-key o.categories/name)})

(defn CategoryForm-data
  [_a]
  {o.categories/id   (ds/gen-key o.categories/id)
   o.categories/name (ds/gen-key o.categories/name)
   o.categories/user
   {o.users/id   (ds/gen-key o.users/id)
    o.users/name (ds/gen-key o.users/name)}})
