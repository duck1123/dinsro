(ns dinsro.ui.forms.nostr.filters.filter-items
  (:require
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes    [m.n.filter-items/id
                     m.n.filter-items/filter
                     m.n.filter-items/type
                     m.n.filter-items/kind
                     m.n.filter-items/event
                     m.n.filter-items/pubkey]
   fo/cancel-route  ["filter-items"]
   fo/id            m.n.filter-items/id
   fo/route-prefix  "create-filter-item"
   fo/title         "Filter Item"})
