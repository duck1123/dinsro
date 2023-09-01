(ns dinsro.mocks.ui.registration
  (:require
   [dinsro.ui.registration :as u.registration]))

;; [[../../ui/registration.cljc]]

(defn IndexPage-data
  []
  {::u.registration/allow-registration true
   ::u.registration/form
   {:username         ""
    :password         ""
    :confirm-password ""}})
