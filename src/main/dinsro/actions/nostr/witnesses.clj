(ns dinsro.actions.nostr.witnesses
  (:require
   [com.fulcrologic.guardrails.core :refer [=> >defn ?]]
   [dinsro.actions.nostr.events :as a.n.events]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.queries.nostr.witnesses :as q.n.witnesses]
   [lambdaisland.glogc :as log]))

(>defn register-witness!
  [event-id run-id]
  [::m.n.events/id ::m.n.runs/id => ::m.n.witnesses/id]
  (log/info :register-witness!/starting {:event-id event-id :run-id run-id})
  (if-let [witness-id (q.n.witnesses/find-by-event-and-run event-id run-id)]
    witness-id
    (q.n.witnesses/create-record
     {::m.n.witnesses/event event-id
      ::m.n.witnesses/run run-id})))

(>defn witness!
  [run-id msg]
  [::m.n.runs/id any? => ::m.n.witnesses/id]
  (log/info :witness!/starting {:run-id run-id :msg msg})
  (let [event-id (a.n.events/register-event! msg)]
    (register-witness! event-id run-id)))
