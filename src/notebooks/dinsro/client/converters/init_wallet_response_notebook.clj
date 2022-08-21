^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.converters.init-wallet-response-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])

  (:require [dinsro.client.converters.init-wallet-response :as c.c.init-wallet-response]))

;; # Init Wallet Response

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def admin-macaroon nil)

(def unknown-fields nil)

(c.c.init-wallet-response/->response admin-macaroon unknown-fields)
