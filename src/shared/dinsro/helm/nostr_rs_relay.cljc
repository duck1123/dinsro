(ns dinsro.helm.nostr-rs-relay)

(defn merge-defaults
  [options]
  (let [{:keys [host]
         :or
         {host "nostr-rs-relay.localtest.me"}} options]
    {:name name
     :host host}))

(defn ->values
  [options]
  (let [options                            (merge-defaults options)
        {host :host
         :or  {host "nostr-rs-relay.localtest.me"}} options]
    {:image   {:tag "latest"}
     :ingress {:hosts [{:host host :paths [{:path "/"}]}]}}))
