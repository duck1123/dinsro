(ns notebooks.test
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.specs :as ds]
   [nextjournal.clerk :as clerk]
   [nextjournal.clerk.viewer :as v]))

(ds/gen-key :xt/id)

(ds/gen-key ::m.accounts/item)

;; (clerk/html "Hello")

;; (v/plotly
;;  {:data [{:z    [[1 2 3]
;;                  [3 2 1]]
;;           :type "surface"}]})

;; 7

;; (+ 39 3)

;; {:foo [1 [2] #{[3] [4] [5 6 7 8]}]}
