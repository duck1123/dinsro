(ns dinsro.ui.initialize
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.semantic-ui.collections.form.ui-form :refer [ui-form]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [com.fulcrologic.semantic-ui.elements.container.ui-container :refer [ui-container]]
   [dinsro.mutations.settings :as mu.settings]
   [dinsro.ui.inputs :as u.inputs]
   [lambdaisland.glogc :as log]))

(defsc InitForm
  [this {::keys [password username]} _ {:keys [container header]}]
  {:css           [[:.container {}]
                   [:.header {:color "red !important"}]]
   :ident         (fn [_] [:component/id ::InitForm])
   :initial-state {::password ""
                   ::username "admin"}
   :query         [::password
                   ::username]
   :route-segment ["first-run"]}
  (dom/div {}
    (dom/div :.ui.inverted.vertical.masthead.center.aligned.segment
      (ui-container {:text true}
        (dom/h1 :.ui.inverted.header "dinsro")))
    (ui-container {:className container}
      (dom/h1 {:classes [:.ui :.header :.center  :.aligned header]}
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
                (log/info :InitForm/primary-clicked {})
                (let [data {:user/username username
                            :user/password password}]
                  (comp/transact! this [`(mu.settings/initialize! ~data)])))})))))))

(def ui-init-form (comp/factory InitForm))
