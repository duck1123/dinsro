(ns dinsro.views.show-user-test
  (:require
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.ui.show-user :as u.show-user]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [taoensso.timbre :as timbre]))

(defsc ShowUserTest
  [_this {:keys [foo]}]
  {:query [:foo]
   :initial-state (fn [_] {:foo "baz"})}
  (dom/div
   (dom/p (str "foo: " foo))
   (u.show-user/ui-show-user)))

(ws/defcard show-user
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root ShowUserTest}))

(defsc FulcroDemo
  [this {:keys [counter]}]
  {:initial-state (fn [_] {:counter 0})
   :ident         (fn [] [::id "singleton"])
   :query         [:counter]}
  (dom/div
   (str "Fulcro counter demo [" counter "]")
   (dom/button {:onClick #(fm/set-value! this :counter (inc counter))} "+")))

(ws/defcard fulcro-demo-card
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-user/ShowUser
    ::ct.fulcro3/initial-state
    (fn [] {::u.show-user/name (ds/gen-key ::m.users/name)
            ::u.show-user/email (ds/gen-key ::m.users/email)})
    ::ct.fulcro3/wrap-root? false}))
