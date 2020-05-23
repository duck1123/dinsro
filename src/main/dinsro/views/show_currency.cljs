(ns dinsro.views.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.currency-accounts :as u.currency-accounts]
   [dinsro.ui.currency-rates :as u.currency-rates]
   [dinsro.ui.show-currency :as u.show-currency]
   [taoensso.timbre :as timbre]))

(defsc ShowCurrencyPage
  [_this {::keys [currency currency-accounts currency-rates]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::currency       {}
                   ::currency-rates {}}
   :query [{::currency          (comp/get-query u.show-currency/ShowCurrency)}
           {::currency-accounts (comp/get-query u.currency-accounts/CurrencyAccounts)}
           {::currency-rates    (comp/get-query u.currency-rates/CurrencyRates)}]
   :route-segment ["currencies" ::m.currencies/id]
   :will-enter
   (fn [app {::m.currencies/keys [id]}]
     (df/load app [::m.currencies/id (int id)] u.show-currency/ShowCurrency
              {:target [:page/id ::page ::currency]})


     ;; (df/load app [::m.currencies/id (int id)] u.currency-accounts/CurrencyAccounts
     ;;          ;; {:target [:page/id ::page ::currency ::currency-accounts]}
     ;;          )

     ;; (df/load app [::m.currencies/id (int id)] u.currency-rates/CurrencyRates
     ;;          ;; {:target [:page/id ::page ::currency ::currency-accounts]}
     ;;          )


     (df/load app :all-rates u.currency-rates/IndexCurrencyRateLine
              {:target [:page/id ::page ::currency-rates
                        ::u.currency-rates/currency-rates
                        ::u.currency-rates/items]})

     (dr/route-immediate (comp/get-ident ShowCurrencyPage {})))}
  (bulma/page
   (bulma/box
    (u.show-currency/ui-show-currency currency))
   (u.currency-accounts/ui-currency-accounts currency-accounts)
   (u.currency-rates/ui-currency-rates currency-rates)))