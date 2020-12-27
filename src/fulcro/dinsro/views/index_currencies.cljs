(ns dinsro.views.index-currencies
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [dinsro.ui.user-currencies :as u.user-currencies]
   [taoensso.timbre :as timbre]))

(defsc IndexCurrenciesPage
  [_this {::keys [currencies]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :all-currencies u.index-currencies/IndexCurrencyLine
               {:target [:component/id
                         ::u.user-currencies/UserCurrencies
                         ::u.user-currencies/currencies
                         ::u.index-currencies/currencies]}))
   :ident (fn [] [:page/id ::page])
   :initial-state {::currencies       {}}
   :query [{::currencies (comp/get-query u.user-currencies/UserCurrencies)}]
   :route-segment ["currencies"]}
  (bulma/page
   (u.user-currencies/ui-user-currencies currencies)))

(def ui-page (comp/factory IndexCurrenciesPage))
