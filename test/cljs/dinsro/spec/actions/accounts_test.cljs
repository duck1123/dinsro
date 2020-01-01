(ns dinsro.spec.actions.accounts-test
  (:require [devcards.core :refer-macros [defcard]]
            [dinsro.spec :as ds]
            [dinsro.spec.actions.accounts :as s.a.accounts]))

(defcard "**Create**")

(defcard s.a.accounts/create-params-valid
  (ds/gen-key ::s.a.accounts/create-params-valid))

(defcard s.a.accounts/create-request
  (ds/gen-key ::s.a.accounts/create-request))

(defcard s.a.accounts/create-response
  (ds/gen-key ::s.a.accounts/create-response))

(defcard "**Read**")

(defcard s.a.accounts/read-request
  (ds/gen-key ::s.a.accounts/read-request))

(defcard s.a.accounts/read-response-success
  (ds/gen-key ::s.a.accounts/read-response-success))

(defcard s.a.accounts/read-response-not-found
  (ds/gen-key ::s.a.accounts/read-response-not-found))

(defcard "**Delete**")

(defcard s.a.accounts/delete-request
  (ds/gen-key ::s.a.accounts/delete-request))

(defcard s.a.accounts/delete-response
  (ds/gen-key ::s.a.accounts/delete-response))

(defcard "**Index**")

(defcard s.a.accounts/index-request
  (ds/gen-key ::s.a.accounts/index-request))

(defcard s.a.accounts/index-response
  (ds/gen-key ::s.a.accounts/index-response))
