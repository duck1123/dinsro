(ns dinsro.actions.status-test)

(deftest status-handler-no-identity
  (let [request {}
        response (a.status/status-handler request)]
    (is (= nil (get-in response [:body :identity])))))
