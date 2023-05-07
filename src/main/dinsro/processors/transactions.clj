(ns dinsro.processors.transactions
  (:require [dinsro.mutations :as mu]))

(defn delete!
  [_env _props]
  {::mu/status :ok})