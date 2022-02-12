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
  [this {::keys [password username]} _ {:keys [container header]}]
  {:ident         (fn [_] [:component/id ::InitForm])
   :query         [::password
                   ::username]
   :css           [[:.container {}]
                   [:.header {:color "red !important"}]]
   :initial-state {::password ""
                   ::username "admin"}
   :route-segment ["first-run"]}
  (dom/div {}
    (dom/div :.ui.inverted.vertical.masthead.center.aligned.segment
      (dom/div :.ui.text.container
        (dom/h1 :.ui.inverted.header "dinsro")))
    (dom/div {:classes [:.ui.container container]}
      (dom/h1 {:classes [:.ui :.header :.center  :.aligned  header]}
              "You must create an administrator account in order to continue")
      (dom/div :.ui.middle.aligned.center.aligned.grid
        (dom/div :.column
          (ui-form {}
            (dom/div :.ui.stacked.segment
              (ui-form-input
               {:value        username
                :onChange     #(fm/set-string! this ::username :event %)
                :label        "Username"
                :icon         "users"
                :iconPosition "left"
                :type         "text"
                :error        false})
              (ui-form-input
               {:value        password
                :onChange     #(fm/set-string! this ::password :event %)
                :label        "Password"
                :icon         "lock"
                :iconPosition "left"
                :type         "password"
                :error        false}))
            (u.inputs/ui-primary-button
             {:content "Submit"}
             {:onClick
              (fn []
                (log/info "clicked")
                (let [data {:user/username username
                            :user/password password}]
                  (comp/transact! this [(mu.settings/initialize! data)])))})))))))

(def ui-init-form (comp/factory InitForm))
