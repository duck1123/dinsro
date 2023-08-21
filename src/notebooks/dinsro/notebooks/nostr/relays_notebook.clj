(ns dinsro.notebooks.nostr.relays-notebook
  (:require
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.queries.nostr.relays :as q.n.relays]))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))

  (a.n.relays/send! relay-id
                    {:kinds [3]
                     :authors ["6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"]})

  (q.n.relays/read-record relay-id)

  (a.n.relays/connect! relay-id)
  (a.n.relays/disconnect! relay-id)

  (map q.n.relays/read-record (q.n.relays/index-ids))

  (some->
   (q.n.relays/index-ids)
   first
   q.n.relays/read-record)

  nil)
