(ns dinsro.spec.views.show-currency
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(s/def :show-currency-view/id          ::ds/id-string)
(s/def :show-currency-view/path-params (s/keys :req-un [:show-currency-view/id]))
(s/def ::view-map                      (s/keys :req-un [:show-currency-view/path-params]))
