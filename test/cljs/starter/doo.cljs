(ns starter.doo
  (:require
   [doo.runner :refer-macros [doo-all-tests]]
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
   dinsro.components.show-account-test
   dinsro.components.show-currency-test
   dinsro.components.status-test
   dinsro.events.accounts-test
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
   dinsro.views.setting-test
   dinsro.views.show-currency-test))

(doo-all-tests #"dinsro\..*(?:-test)$")
