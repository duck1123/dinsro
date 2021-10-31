(ns dinsro.ui.show-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.rate-source-transactions :as u.rate-source-transactions]
   [taoensso.timbre :as log]))

(defsc ShowRateSource
  [_this {::m.rate-sources/keys [id name]}]
  {:query         [::m.rate-sources/id ::m.rate-sources/name]
   :ident         ::m.rate-sources/id
   :initial-state {::m.rate-sources/id   nil
                   ::m.rate-sources/name ""}}
  (dom/div {}
    (dom/p name)
    (dom/p id)
    (u.buttons/ui-delete-rate-source-button {::m.rate-sources/id id})))

(def ui-show-rate-source (comp/factory ShowRateSource))

(defsc ShowRateSourcePage
  [_this {::keys [id rate-source transactions]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::id           1
                   ::rate-source  {}
                   ::transactions {}}
   :query         [::id
                   {::rate-source (comp/get-query ShowRateSource)}
                   {::transactions (comp/get-query u.rate-source-transactions/RateSourceTransactions)}]
   :route-segment ["rate-sources" ::m.rate-sources/id]
   :will-enter
   (fn [app {::m.rate-sources/keys [id]}]
     (df/load app [::m.rate-sources/id (int id)] ShowRateSource
              {:target [:page/id ::page ::rate-source]})

     (dr/route-immediate (comp/get-ident ShowRateSourcePage {})))}
  (bulma/page
   (bulma/box
    (ui-show-rate-source rate-source))
   (u.rate-source-transactions/ui-rate-source-transactions
    {::m.rate-sources/id id}
    transactions)))
