(ns dinsro.actions.ln.invoices
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.invoices :as m.ln.invoices]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.invoices :as q.ln.invoices]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(>defn fetch
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (a.ln.nodes/get-client node)]
    (<!! (c.lnd/list-invoices client))))

(>defn update!
  [node-id]
  [::m.ln.nodes/id => any?]
  (log/info "Updating invoices")
  (if-let [node (q.ln.nodes/read-record node-id)]
    (doseq [params (:invoices (fetch node))]
      (let [{:keys [addIndex]} params]
        (if-let [old-invoice-id (q.ln.invoices/find-id-by-node-and-index node-id addIndex)]
          (if-let [old-invoice (q.ln.invoices/read-record old-invoice-id)]
            (let [params     (assoc params ::m.ln.invoices/node node-id)
                  params     (m.ln.invoices/prepare-params params)
                  new-params (merge old-invoice params)]
              (log/infof "has an invoice: %s" new-params)
              (q.ln.invoices/update! new-params))
            (throw (RuntimeException. "Failed to find invoice")))
          (let [params (assoc params ::m.ln.invoices/node node-id)
                params (m.ln.invoices/prepare-params params)]
            (q.ln.invoices/create-record params)))))
    (throw (RuntimeException. "Failed to find node"))))

(defn add-invoice
  [data]
  (let [{{node-id ::m.ln.nodes/id} ::m.ln.invoices/node} data
        node                                             (q.ln.nodes/read-record node-id)
        client                                           (a.ln.nodes/get-client node)]
    (log/infof "Adding invoice: %s" data)
    (let [{::m.ln.invoices/keys [value memo]} data]
      (<!! (c.lnd/add-invoice client value memo)))))

(comment
  (def node-alice
    (q.ln.nodes/read-record
     (q.ln.nodes/find-id-by-user-and-name
      (q.users/find-eid-by-name "alice")
      "lnd-alice")))
  (def node-bob
    (q.ln.nodes/read-record
     (q.ln.nodes/find-id-by-user-and-name
      (q.users/find-eid-by-name "bob")
      "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (def client (a.ln.nodes/get-client node))

  (def inv "lnbcrt13450n1psu34h3pp5v6rw3yf2f06w2dmlkkm8jc33nl9rcjayzer6k8fdhsn0uv2gg0gqdq8w3jhxaqcqzpgxqrrsssp5cyquv9vqyykwcxrreyxnlpp4040rfkkapnrddzye29qhdt8tqehq9qyyssq2kkxcnuu9rfenhcmsser80jqw4ttenv503n6fhwrz8yxpctmtgtkhkrj5kpzfjfvfl2ud0rldpd5pgu3apvfcg8p8q3036wkz5nf5ugqh43y62")

  (c.lnd/list-invoices (a.ln.nodes/get-client node))

  (<!! (c.lnd/decode-pay-req client inv))

  (<!! (c.lnd/lookup-invoice client inv))

  (let [client (a.ln.nodes/get-invoices-client node)]
    (<!! (c.lnd/lookup-invoice client inv)))

  (let [client (a.ln.nodes/get-client node-alice)]
    (<!! (c.lnd/send-payment-sync client "lnbcrt50u1psu56pkpp5y8strxe68vm99j37f63dxmuap76f980xqs052t0eh6ztfsctvumsdqh235xjueqd9ejqcfqd4jk6mccqzpgxqyz5vqsp5aagvk9yveakxv7af38eajtq44c5f8xlnzgranava8cq5daknknns9qyyssqyhkjl6q4xts2yw8gjm2u8a028pdyy3043efkmn5cmq0kz4re64jjglkdgpqwftwwlax8gh6nn3qwxscheg5rxkdtvfvnwv8y76qu7wqqn4ugvy")))

  (q.ln.invoices/index-ids)
  (q.ln.invoices/index-records)

  (map q.ln.invoices/delete!  (q.ln.invoices/index-ids))

  (fetch node)
  (update! (::m.ln.nodes/id node))
  (def iresponse (async/<!! (fetch node)))
  (first (:invoices iresponse))

  nil)
