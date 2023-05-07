^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.client.converters.init-wallet-response-notebook
  (:require
   [dinsro.client.converters.init-wallet-response :as c.c.init-wallet-response]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Init Wallet Response

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def admin-macaroon nil)

(def unknown-fields nil)

(try
  (c.c.init-wallet-response/->response admin-macaroon unknown-fields)
  (catch Exception ex ex))
