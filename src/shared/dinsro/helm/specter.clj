(ns dinsro.helm.specter)

(defn ->values
  [{:keys [name] :as _options}]
  {:image {:tag "v1.7.2"}
   :ingress     {:hosts [{:host  (str "specter-" name ".localhost")
                          :paths [{:path "/"}]}]}
   :persistence {:storageClassName "local-path"}})
