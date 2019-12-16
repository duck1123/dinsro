(ns dinsro.components.user-accounts
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-user-account :as c.f.add-user-account]
            [dinsro.components.index-accounts :as c.index-accounts]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec section vector?
  [user-id pos-int? accounts ::s.accounts/item]
  [:div.box
   [:h2
    (tr [:accounts])
    [c/show-form-button ::c.f.add-user-account/shown? ::c.f.add-user-account/set-shown?]]
   [c.f.add-user-account/add-user-account user-id]
   [:hr]
   [c.index-accounts/index-accounts accounts]])
