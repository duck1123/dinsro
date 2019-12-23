(ns dinsro.components.forms.login
  (:require [ajax.core :as ajax]
            [cemerick.url :as url]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(defn-spec form vector?
  [return-to string?]
  (let [form-data @(rf/subscribe [::form-data])]
    [:form.is-centered
     [c.debug/debug-box form-data]
     [c/email-input (tr [:email]) ::email ::set-email]
     [c/password-input (tr [:password]) ::password ::set-password]
     [c/primary-button (tr [:login]) [::e.authentication/do-authenticate form-data return-to]]]))
