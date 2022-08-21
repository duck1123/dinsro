^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.client.converters.list-accounts-response-notebook
  (:require
   [dinsro.client.converters.list-accounts-response :as c.c.list-accounts-response]
   [dinsro.client.scala :as cs]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # List Accounts Response


^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def obj (c.c.list-accounts-response/->response))

(cs/->record obj)
