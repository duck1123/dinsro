(ns dinsro.model.users-test
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.test-helpers :refer [key-card]]))

(key-card ::m.users/role)
(key-card ::m.users/item)
(key-card ::m.users/ident)
