(ns dinsro.model.nostr.author-filters)

(>def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(>def ::filter uuid?)
(defattr filter ::filter :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.filters/id
   ao/schema           :production
   ::report/column-EQL {::filter [::m.n.filters/id ::m.n.filters/address]}})

(>def ::pubkey uuid?)
(defattr pubkey ::pubkey :ref
  {ao/identities       #{::id}
   ao/target           ::m.n.pubkeys/id
   ao/schema           :production
   ::report/column-EQL {::pubkey [::m.n.pubkeys/id ::m.n.pubkeys/address]}})
