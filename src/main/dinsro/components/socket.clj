(ns dinsro.components.socket
  (:require
   [taoensso.sente :as sente]
   [taoensso.sente.packers.transit :as sente-transit]
   [taoensso.sente.server-adapters.http-kit :as http-kit]))

(let [chsk-server
      (sente/make-channel-socket-server!
       (http-kit/get-sch-adapter)
       {:packer        (sente-transit/get-transit-packer)
        :csrf-token-fn nil})
      {:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]} chsk-server]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv)
  (def chsk-send!                    send-fn)
  (def connected-uids                connected-uids))

(defn send-data [data]
  (doseq [uid (:any @connected-uids)]
    (chsk-send! uid [:some/broadcast data])))

(comment
  (send-data {:some-new-data true}))
