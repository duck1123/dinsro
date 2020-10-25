(ns dinsro.views.about
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.store :as st]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(defn page [_store _match]
  [:section.section>div.container>div.content
   [:h1 "About"]
   [:img {:src "/img/warning_clojure.png"}]])

(s/fdef page
  :args (s/cat :store #(instance? st/Store %)
               :match #(instance? rc/Match %))
  :ret vector?)
