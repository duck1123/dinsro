(ns dinsro.components.user-accounts
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.forms.add-user-account :as c.f.add-user-account]
            [dinsro.components.index-accounts :as c.index-accounts]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec section vector?
  [user-id ::ds/id
   accounts (s/coll-of ::s.accounts/item)]
  [:div.box
   [:h2
    (tr [:accounts])
    [c/show-form-button ::c.f.add-user-account/shown? ::c.f.add-user-account/set-shown?]]
   [c.f.add-user-account/form user-id]
   [:hr]
   [c.index-accounts/index-accounts accounts]])
