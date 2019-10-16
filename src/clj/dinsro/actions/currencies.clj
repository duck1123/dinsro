(ns dinsro.actions.currencies
  (:require [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn index-currencies
  [request]
  (http/ok {:currencies [{:id 1 :name "sats" :is-primary true}
                         {:id 2 :name "dollars" :exchange 0.6}]}))
