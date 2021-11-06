(ns dinsro.test
  (:require
   [dinsro.specs :as ds]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]))

(clerk/html "Hello")

(v/plotly
 {:data [{:z    [[1 2 3]
                 [3 2 1]]
          :type "surface"}]})

(ds/gen-key :db/id)

7

(+ 39 3)

{:foo [1 [2] #{[3] [4] [5 6 7 8]}]}
