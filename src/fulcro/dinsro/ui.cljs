(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   ;; [com.fulcrologic.fulcro-css.css-injection :as inj]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.routing :as routing]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.navbar :as u.navbar]
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

(defsc Root [this {:root/keys [router] :as props}]
  {:query [::m.accounts/id
           ::m.users/id
           ::m.currencies/id
           ::m.rates/id
           ::m.rate-sources/id
           ::m.categories/id
           ::m.transactions/id

           {:root/router (comp/get-query routing/RootRouter)}]

   :initial-state {:root/router {}
                   ::m.accounts/id sample/account-map
                   ::m.users/id sample/user-map
                   ::m.currencies/id sample/currency-map
                   ::m.rates/id sample/rate-map
                   ::m.rate-sources/id sample/rate-source-map
                   ::m.categories/id sample/category-map
                   ::m.transactions/id sample/category-map}}
  (do
    (js/console.log props)
    (let [top-router-state (or (uism/get-active-state this ::routing/RootRouter) :initial)]
      (dom/div
       ;; (inj/style-element {:component Root})
       (u.navbar/ui-navbar)
       (dom/div
        (dom/button {:onClick #(dr/change-route this ["admin"])} "admin")
        (dom/button {:onClick #(dr/change-route this [""])} "home")
        (dom/button {:onClick #(dr/change-route this ["accounts"])} "accounts")
        (dom/button {:onClick #(dr/change-route this ["currencies"])} "currencies")
        (dom/button {:onClick #(dr/change-route this ["rates"])} "rates")
        (dom/button {:onClick #(dr/change-route this ["categories"])} "categories")
        (dom/button {:onClick #(dr/change-route this ["rate-sources"])} "rate-sources")
        (dom/button {:onClick #(dr/change-route this ["transactions"])} "transactions")
        (dom/button {:onClick #(dr/change-route this ["users"])} "users")
        (dom/button {:onClick #(dr/change-route this ["login"])} "login"))

       (dom/div
        :.container
        (if (= :initial top-router-state)
          (dom/div :.loading "Loading...")
          (routing/ui-root-router router)))))))
