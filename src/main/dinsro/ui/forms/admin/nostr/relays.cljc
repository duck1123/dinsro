(ns dinsro.ui.forms.admin.nostr.relays
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [lambdaisland.glogc :as log]))

(def submit-button
  {:type   :button
   :local? true
   :label  "Submit"
   :action (fn [this _]
             (let [props   (comp/props this)
                   address (::m.n.relays/address props)]
               (log/info :submit-button/clicked {:address address})
               (comp/transact! this
                 [`(mu.n.relays/submit! {::m.n.relays/address ~address})])))})

(form/defsc-form NewRelayForm [_this _props]
  {fo/action-buttons [::submit]
   fo/attributes     [m.n.relays/address]
   fo/cancel-route   ["relays"]
   fo/controls       {::submit submit-button}
   fo/id             m.n.relays/id
   fo/route-prefix   "new-relay"
   fo/title          "Relay"})
