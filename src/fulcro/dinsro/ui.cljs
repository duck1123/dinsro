(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.navbar :as u.navbar]
   [dinsro.views.home :as v.home]
   [dinsro.views.index-accounts :as v.index-accounts]
   [taoensso.timbre :as timbre]))

(defn email-input-f
  []
  (dom/input {:placeholder "email"}))

(defsc FooItem [_this {:foo/keys [id name]}]
  {:query [:foo/id :foo/name]
   :ident :foo/id}
  (dom/div id " " name))

(defsc Foo [_this _props]
  {:query [{:foo-list/foo (comp/get-query FooItem)}]}
  (dom/div "Food"))

(def ui-foo (comp/factory Foo))

(defsc Root [_this {:root/keys [foo-picker]}]
  {:query [{:root/foo-picker (comp/get-query Foo)}]
   :initial-state (fn [_] {:root/foo-picker {:foo/id 1}
                           :page-name :home})}
  (dom/div
   (u.navbar/ui-navbar)
   (dom/div
    :.container
    (v.home/ui-page)
    (comment (v.index-accounts/ui-page))
    (dom/div "Foo")
    (ui-foo foo-picker)
    (ui-foo foo-picker)
    (dom/div "Bar"))))
