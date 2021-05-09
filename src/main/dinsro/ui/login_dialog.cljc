(ns dinsro.ui.login-dialog
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:cljs [com.fulcrologic.fulcro.mutations :as m])
   [com.fulcrologic.rad.authorization :as auth]
   #?(:cljs [com.fulcrologic.semantic-ui.modules.modal.ui-modal :refer [ui-modal]])
   #?(:cljs [com.fulcrologic.semantic-ui.modules.modal.ui-modal-header :refer [ui-modal-header]])
   #?(:cljs [com.fulcrologic.semantic-ui.modules.modal.ui-modal-content :refer [ui-modal-content]])
   #?(:cljs [dinsro.model.accounts :as m.accounts])
   [taoensso.timbre :as log]))

(defsc LoginForm
  [this {:ui/keys [username password]} {:keys [visible?]}]
  {:query         [:ui/username
                   :ui/password]
   :initial-state {:ui/username "tony@example.com"
                   :ui/password "letmein"}

   ::auth/provider      :local
   ::auth/check-session `account/check-session
   ::auth/logout        `account/logout

   :ident (fn [] [:component/id ::LoginForm])}
  #?(:clj
     (comment this username password visible?)
     :cljs
     (ui-modal
      {:open (boolean visible?) :dimmer true}
      (ui-modal-header {} "Please Log In")
      (ui-modal-content {}
        (dom/div :.ui.form
          (dom/div :.ui.field
            (dom/label "Username")
            (dom/input {:type     "email"
                        :onChange (fn [evt] (m/set-string! this :ui/username :event evt))
                        :value    (or username "")}))
          (dom/div :.ui.field
            (dom/label "Password")
            (dom/input {:type     "password"
                        :onChange (fn [evt] (m/set-string! this :ui/password :event evt))
                        :value    (or password "")}))
          (dom/div :.ui.primary.button
            {:onClick
             (fn [] (comp/transact!
                     this
                     [(m.accounts/login {:username username :password password})]))}
            "Login"))))))
