(ns starter.doo
  (:require [doo.runner :refer-macros [doo-all-tests]]
            dinsro.core-test
            dinsro.components.admin-index-rate-sources-test
            dinsro.components.forms.add-user-transaction-test
            dinsro.components.forms.create-transaction-test
            dinsro.components.index-transactions-test
            dinsro.components.show-account-test
            dinsro.components.show-currency-test
            dinsro.components.status-test
            dinsro.events.accounts-test
            dinsro.events.transactions-test
            dinsro.spec.accounts-test
            dinsro.spec.actions.categories-test
            dinsro.spec.currencies-test
            dinsro.spec.transactions-test
            dinsro.views.about-test
            dinsro.views.login-test))

(doo-all-tests #"dinsro\..*(?:-test)$")
