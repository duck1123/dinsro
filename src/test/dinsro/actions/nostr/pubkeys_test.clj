(ns dinsro.actions.nostr.pubkeys-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   ;; [dinsro.actions.authentication :as a.authentication]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   ;; [dinsro.model.users :as m.users]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   ;; [dinsro.specs :as ds]
   [dinsro.test-helpers :as th]
   [fulcro-spec.check :as _]
   [fulcro-spec.core :refer [assertions]]))

;; [[../../../../main/dinsro/actions/nostr/pubkeys.clj][Pubkey Actions]]

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest register-pubkey!
  (let [pubkey-hex "6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"]
    (assertions
     "Should initially be missing"
     (q.n.pubkeys/find-by-hex pubkey-hex) => nil

     "Should return an id"
     (a.n.pubkeys/register-pubkey! pubkey-hex) =check=> (_/is?* uuid?)

     "Should be created after registration"
     (q.n.pubkeys/find-by-hex pubkey-hex) =check=> (_/is?* uuid?))))

(deftest process-pubkey-message!
  (let [event-type "EVENT"
        code       "adhoc"
        id         "1faef913d305a50e1fb9ff641dbc85854d3c6c272493875d1e7f22f6439a80ef"
        pubkey     "6bda57c3323ac4d8b4ca32729d07f1707b60df1c0625e7acab3cefefb001cf28"
        sig        "3be48b8c655cd66c3862f5c5b873d570b004f84ec1879c1b86f89d561641e596caa5f0c997b2a9e34cb5e5bf66e6b32390864b4318e573de782f75c3370369b1"
        content    "{\"name\":\"bob\",\"about\":\"Bob test account for Dinsro\",\"nip05\":\"bob@duck1123.com\",\"lud06\":\"lnurl1dp68gurn8ghj7cm0d9hx7uewd9hj7tnhv4kxctttdehhwm30d3h82unvwqhkgatrdvrwrevc\",\"lud16\":\"duck@coinos.io\",\"picture\":\"https://upload.wikimedia.org/wikipedia/en/a/a3/Bobdobbs.png\"}"
        body       {"content"    content
                    "created_at" 1674661591
                    "id"         id
                    "kind"       0
                    "pubkey"     pubkey
                    "sig"        sig
                    "tags"       []}]
    (assertions
     (a.n.pubkeys/process-pubkey-message! event-type code body)  =check=> (_/is?* uuid?))

    (let [id (q.n.pubkeys/find-by-hex pubkey)]
      (assertions
       (q.n.pubkeys/read-record id)
       =check=>
       (_/embeds?*
        #::m.n.pubkeys{:id      id
                       :name    "bob"
                       :about   "Bob test account for Dinsro"
                       :nip05   "bob@duck1123.com"
                       :lud06   "lnurl1dp68gurn8ghj7cm0d9hx7uewd9hj7tnhv4kxctttdehhwm30d3h82unvwqhkgatrdvrwrevc"
                       :lud16   "duck@coinos.io"
                       :picture "https://upload.wikimedia.org/wikipedia/en/a/a3/Bobdobbs.png"
                       :website nil})))))
