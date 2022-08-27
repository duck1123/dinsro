(ns dinsro.client.converters.list-channels-request
  (:require
   [dinsro.client.scala :as cs])
  (:import
   lnrpc.ListChannelsRequest))

(defn ->obj
  "https://bitcoin-s.org/api/lnrpc/ListChannelsRequest.html"
  ([]
   (let [active-only false]
     (->obj active-only)))

  ([active-only]
   (let [inactive-only false]
     (->obj active-only inactive-only)))

  ([active-only inactive-only]
   (let [public-only false]
     (->obj active-only inactive-only public-only)))

  ([active-only inactive-only public-only]
   (let [private-only false]
     (->obj active-only inactive-only public-only private-only)))

  ([active-only inactive-only public-only private-only]
   (let [peer (cs/empty-byte-string)]
     (->obj active-only inactive-only public-only private-only peer)))

  ([active-only inactive-only public-only private-only peer]
   (let [unknown-fields (cs/empty-unknown-field-set)]
     (->obj
      active-only inactive-only public-only private-only peer unknown-fields)))

  ([active-only inactive-only public-only private-only peer unknown-fields]
   (ListChannelsRequest.
    active-only
    inactive-only
    public-only
    private-only
    peer
    unknown-fields)))
