(ns dinsro.views.show-rate-source
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.rate-source-transactions :as u.rate-source-transactions]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.show-rate-source :as u.show-rate-source]
   [taoensso.timbre :as timbre]))

(defsc ShowRateSourcePage
  [_this {::keys [id rate-source transactions]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::id 1
                   ::rate-source  {}
                   ::transactions {}}
   :query [{::rate-source  (comp/get-query u.show-rate-source/ShowRateSource)}
           {::transactions (comp/get-query u.rate-source-transactions/RateSourceTransactions)}]
   :route-segment ["rate-sources" ::m.rate-sources/id]
   :will-enter
   (fn [app {::m.rate-sources/keys [id]}]
     (df/load app [::m.rate-sources/id (int id)] u.show-rate-source/ShowRateSource
              {:target [:page/id ::page ::rate-source]})

     (dr/route-immediate (comp/get-ident ShowRateSourcePage {})))}
  (bulma/page
   (bulma/box
    (u.show-rate-source/ui-show-rate-source rate-source))
   (u.rate-source-transactions/ui-rate-source-transactions
    {::m.rate-sources/id id}
    transactions)))
