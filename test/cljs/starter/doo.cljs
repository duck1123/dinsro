(ns starter.doo
  (:require
   [cljs.repl :as repl]
   [cljs.spec.alpha :as s]
   [devtools.core :as devtools]
   dinsro.core-test
   dinsro.components-test
   dinsro.components.admin-index-accounts-test
   dinsro.components.admin-index-categories-test
   dinsro.components.admin-index-rate-sources-test
   dinsro.components.currency-rates-test
   dinsro.components.forms.add-user-transaction-test
   dinsro.components.forms.create-transaction-test
   dinsro.components.forms.settings-test
   dinsro.components.index-transactions-test
   dinsro.components.rate-chart-test
   dinsro.components.show-account-test
   dinsro.components.show-currency-test
   dinsro.components.status-test
   dinsro.events.accounts-test
   dinsro.events.rates-test
   dinsro.events.transactions-test
   dinsro.spec.accounts-test
   dinsro.spec.actions.accounts-test
   dinsro.spec.actions.categories-test
   dinsro.spec.actions.rate-sources-test
   dinsro.spec.actions.rates-test
   dinsro.spec.currencies-test
   dinsro.spec.rates-test
   dinsro.spec.transactions-test
   dinsro.views.about-test
   dinsro.views.admin-test
   dinsro.views.index-accounts-test
   dinsro.views.index-transactions-test
   dinsro.views.login-test
   dinsro.views.settings-test
   dinsro.views.show-currency-test
   [doo.runner :refer-macros [doo-all-tests]]
   [expound.alpha :as expound]
   [mount.core :as mount]
   [orchestra-cljs.spec.test :as stest]
   [taoensso.timbre :as timbre]))

(extend-protocol IPrintWithWriter
  js/Symbol
  (-pr-writer [sym writer _]
    (-write writer (str "\"" (.toString sym) "\""))))

(extend-type ExceptionInfo
  IPrintWithWriter
  (-pr-writer [o writer opts]
    (-write writer (repl/error->str o))))

(set! s/*explain-out* expound/printer)

(defn error-handler [_message _url _line _column e]
  (js/console.error e)
  (print (repl/error->str e))
  true)

(set! (.-onerror js/window) error-handler)

(enable-console-print!)

(devtools/install!)

(mount/defstate instrument
  :start (stest/instrument))

(doo-all-tests #"dinsro\..*(?:-test)$")
