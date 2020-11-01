(ns dinsro.specs.views.show-user
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.specs :as ds]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(s/def :show-user-view/id          ::ds/id-string)
(s/def :show-user-view/path-params (s/keys :req-un [:show-user-view/id]))
(s/def ::view-map                  (s/keys :req-un [:show-user-view/path-params]))
