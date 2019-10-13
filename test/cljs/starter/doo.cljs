(ns starter.doo
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            dinsro.components.about-test
            dinsro.components.register-test
            dinsro.core-test
            dinsro.view-test))

(doo-all-tests #"dinsro\..*(?:-test)$")
