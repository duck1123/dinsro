^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.ln.invoices-notebook
  (:require
   [dinsro.actions.ln.invoices :as a.ln.invoices]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.ln.invoices :as q.ln.invoices]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Invoice Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def node-alice2 n.lnd/node-alice)

(def inv "lnbcrt13450n1psu34h3pp5v6rw3yf2f06w2dmlkkm8jc33nl9rcjayzer6k8fdhsn0uv2gg0gqdq8w3jhxaqcqzpgxqrrsssp5cyquv9vqyykwcxrreyxnlpp4040rfkkapnrddzye29qhdt8tqehq9qyyssq2kkxcnuu9rfenhcmsser80jqw4ttenv503n6fhwrz8yxpctmtgtkhkrj5kpzfjfvfl2ud0rldpd5pgu3apvfcg8p8q3036wkz5nf5ugqh43y62")
(def inv2 "lnbcrt50u1psu56pkpp5y8strxe68vm99j37f63dxmuap76f980xqs052t0eh6ztfsctvumsdqh235xjueqd9ejqcfqd4jk6mccqzpgxqyz5vqsp5aagvk9yveakxv7af38eajtq44c5f8xlnzgranava8cq5daknknns9qyyssqyhkjl6q4xts2yw8gjm2u8a028pdyy3043efkmn5cmq0kz4re64jjglkdgpqwftwwlax8gh6nn3qwxscheg5rxkdtvfvnwv8y76qu7wqqn4ugvy")

(comment
  (q.ln.invoices/index-ids)
  (q.ln.invoices/index-records)

  (map q.ln.invoices/delete!  (q.ln.invoices/index-ids))

  (a.ln.invoices/update! (::m.ln.nodes/id n.lnd/node))

  nil)
