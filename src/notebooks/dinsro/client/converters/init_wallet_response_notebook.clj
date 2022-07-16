(ns dinsro.client.converters.init-wallet-response-notebook
  (:require [dinsro.client.converters.init-wallet-response :as c.c.init-wallet-response]))

(def admin-macaroon nil)

(def unknown-fields nil)

(c.c.init-wallet-response/->response admin-macaroon unknown-fields)
