(ns dinsro.helm.nostream)

(defn merge-defaults
  [options]
  (let [{:keys [host tag]
         :or
         {host "nostream.localtest.me"
          tag  "main"}} options]
    {:host host
     :tag  tag}))

(defn ->values
  [options]
  (let [options            (merge-defaults options)
        {:keys [host tag]} options]
    {:image   {:tag tag}
     :ingress {:hosts [{:host host :paths [{:path "/"}]}]}}))
