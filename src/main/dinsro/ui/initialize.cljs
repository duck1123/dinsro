(ns dinsro.ui.initialize
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.mutations.settings :as mu.settings]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc InitForm
  [this {::keys [password username]}]
  {:ident         (fn [_] [:component/id ::InitForm])
   :query         [::password
                   ::username]
   :initial-state {::password ""
                   ::username "admin"}}
  (dom/div :.ui
    (dom/h1 {} "You must create an administrator account in order to continue")
    (ui-form {}
      (ui-form-input
       {:value    username
        :onChange #(fm/set-string! this ::username :event %)
        :label    "Username"
        :type     "text"
        :error    false})
      (ui-form-input
       {:value    password
        :onChange #(fm/set-string! this ::password :event %)
        :label    "Password"
        :type     "password"
        :error    false})
      (u.inputs/ui-primary-button
       {:content "Submit"}
       {:onClick
        (fn []
          (log/info "clicked")
          (let [data {:user/username username
                      :user/password password}]
            (comp/transact! this [(mu.settings/initialize! data)])))}))))

(def ui-init-form (comp/factory InitForm))
