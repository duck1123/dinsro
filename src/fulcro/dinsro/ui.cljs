(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.routing :as routing]
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

(defsc Root [this {:root/keys [router]}]
  {:query [{:root/router (comp/get-query routing/RootRouter)}
           [::uism/asm-id ::routing/RootRouter]]
   :initial-state (fn [_] {:root/router {}})}
  (let [top-router-state (or (uism/get-active-state this ::routing/RootRouter) :home)]
    (dom/div
     (u.navbar/ui-navbar)
     (dom/div
      :.container
      (if (= :initial top-router-state)
        (dom/div :.loading "Loading...")
        (routing/ui-root-router router))
      (comment (v.home/ui-page))
      (comment (v.index-accounts/ui-page))))))
