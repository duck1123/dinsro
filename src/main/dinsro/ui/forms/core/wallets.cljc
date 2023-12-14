(ns dinsro.ui.forms.core.wallets
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.core.wallets :as m.c.wallets]
   [dinsro.mutations.core.wallets :as mu.c.wallets]
   [dinsro.ui.debug :as u.debug]
   #_[dinsro.ui.pickers :as u.pickers]
   [lambdaisland.glogc :as log]))

(def log-props? true)
(def override-form? true)

(def create-button
  {:type   :button
   :local? true
   :label  "Create"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [`(mu.c.wallets/create! ~props)])))})

(form/defsc-form NewForm
  [this props]
  {fo/action-buttons (concat [::create] form/standard-action-buttons)
   fo/attributes     [m.c.wallets/name
                      m.c.wallets/user]
   fo/controls       (merge form/standard-controls {::create create-button})
   ;; fo/field-styles   {::m.c.wallets/node :pick-one
   ;;                    ::m.c.wallets/user :pick-one}
   ;; fo/field-options  {::m.c.wallets/node u.pickers/node-picker
   ;;                    ::m.c.wallets/user u.pickers/user-picker}
   fo/id             m.c.wallets/id
   fo/route-prefix   "new-wallet"
   fo/title          "New Wallet"}
  (log/info :NewWalletForm/creating {:props props})
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (dom/div {} "form"))
    (when log-props?
      (u.debug/ui-props-logger props))))
