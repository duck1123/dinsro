(ns dinsro.views.show-currency
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.show-currency :as u.show-currency]
   [taoensso.timbre :as log]))

(defsc ShowCurrencyPage
  [_this {::keys [currency]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::currency {}}
   :query         [{::currency (comp/get-query u.show-currency/ShowCurrencyFull)}]
   :route-segment ["currencies" :id]
   :will-enter
   (fn [app {:keys [id]}]
     (when (seq id)
       (df/load app [::m.currencies/id (new-uuid id)] u.show-currency/ShowCurrencyFull
                {:target [:page/id ::page ::currency]}))
     (dr/route-immediate (comp/get-ident ShowCurrencyPage {})))}
  (if (::m.currencies/id currency)
    (u.show-currency/ui-show-currency-full currency)
    (dom/p "not loaded")))
