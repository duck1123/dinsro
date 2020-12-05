(ns dinsro.specs.views.show-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs :as ds]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(s/def :show-account-view/id          ::ds/id-string)
(s/def :show-account-view/path-params (s/keys :req-un [:show-account-view/id]))
(s/def ::view-map (s/keys :req-un [:show-account-view/path-params]))
