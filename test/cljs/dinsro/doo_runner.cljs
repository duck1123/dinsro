(ns dinsro.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [dinsro.core-test]))

(doo-tests 'dinsro.core-test)
