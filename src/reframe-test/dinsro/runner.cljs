(ns dinsro.runner)

(defn start
  [])

(defn stop
  [done]
  (done))

(defn ^:export init []
  (start))
