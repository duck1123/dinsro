(ns starter.doo
  (:require [doo.runner :refer-macros [doo-tests doo-all-tests]]
            dinsro.components-test
            dinsro.core-test))

(doo-all-tests #"dinsro\..*(?:-test)$")
