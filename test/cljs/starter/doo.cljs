(ns starter.doo
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            dinsro.components.register-test
            dinsro.core-test
            dinsro.views.about-test
            dinsro.views.home-test))

(doo-all-tests #"dinsro\..*(?:-test)$")
