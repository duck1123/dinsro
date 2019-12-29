(ns starter.doo
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            dinsro.core-test
            dinsro.components.forms.add-user-transaction-test
            dinsro.components.forms.create-transaction-test
            dinsro.components.index-transactions-test
            dinsro.components.show-account-test
            dinsro.events.accounts-test
            dinsro.events.transactions-test
            dinsro.spec.accounts-test
            dinsro.spec.currencies-test
            dinsro.spec.transactions-test
            dinsro.views.about-test
            dinsro.views.home-test
            dinsro.views.login-test
            ;; dinsro.views.register-test
            ))

(doo-all-tests #"dinsro\..*(?:-test)$")
