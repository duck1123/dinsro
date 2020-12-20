(ns dinsro.views.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.show-currency :as u.show-currency]
   [taoensso.timbre :as timbre]))

(defsc ShowCurrencyPage
  [_this {::keys [currency]}]
  {:ident (fn [] [:page/id ::page])
   :initial-state {::currency {}}
   :query [{::currency (comp/get-query u.show-currency/ShowCurrency)}]
   :route-segment ["currencies" ::m.currencies/id]
   :will-enter (fn [app {::m.currencies/keys [id]}]
                 (df/load app [::m.currencies/id (int id)] u.show-currency/ShowCurrency
                          {:target [:page/id ::page ::currency]})
                 (dr/route-immediate (comp/get-ident ShowCurrencyPage {})))}
  (bulma/section
   (bulma/container
    (bulma/content
     (bulma/box
      (u.show-currency/ui-show-currency currency))))))
