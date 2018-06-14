(ns dinsro.general-functions.validations
    (:require
     [compojure.api.coercion.spec :as cs]))

(def spec
  (cs/create-coercion
   {:body {:default cs/default-conforming}
    :string {:default cs/default-conforming}
    :response {:default cs/default-conforming}}))
