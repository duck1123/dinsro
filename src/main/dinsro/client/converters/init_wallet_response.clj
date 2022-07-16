(ns dinsro.client.converters.init-wallet-response
  (:import
   lnrpc.InitWalletResponse))

(defn ->response
  [admin-macaroon
   unknown-fields]
  (InitWalletResponse.
   admin-macaroon
   unknown-fields))
