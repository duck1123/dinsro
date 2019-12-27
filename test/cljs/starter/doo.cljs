(ns starter.doo
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            dinsro.core-test
            dinsro.spec.accounts-test
            dinsro.spec.currencies-test
            dinsro.views.about-test
            dinsro.views.home-test
            dinsro.views.login-test
            ;; dinsro.views.register-test
            ))

(doo-all-tests #"dinsro\..*(?:-test)$")
