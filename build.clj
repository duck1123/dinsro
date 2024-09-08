(ns build
  "dinsro build script"
  (:require
   [clojure.java.io :as io]
   [clojure.tools.build.api :as b]
   [nextjournal.clerk.config :as config]
   [org.corfield.build :as bb]))

(def lib 'duck1123/dinsro)
(def version (format "4.0.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:aliases [:notebooks]}))
(def jar-file (bb/default-jar-file lib version))

(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

(defn print-version
  [_opts]
  (print version))

(defn run
  [_opts]
  (println "run")
  (b/process {:command-args ["java" "-jar" jar-file]}))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/run-tests)
      (bb/clean)
      (bb/jar)))

(defn install "Install the JAR locally." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/install)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/deploy)))

(defn clean [_]
  (b/delete {:path "target"}))

(defn eastwood "Run Eastwood." [opts]
  (-> opts (bb/run-task [:eastwood])))

(defn package-asset-map [_]
  (let [lookup-url "https://storage.googleapis.com/nextjournal-cas-eu/assets/4AHiU1U3uJCbnLcyv1qxbfFjKqfjz8RqQBZ5bxyLhH8dnxqWSc8tsQHfzCxGggVwwRv8EvZYoVoSuMTm3UdddqoN-viewer.js"
        #_#_asset-map (slurp lookup-url)]
    (io/make-parents "target/classes/clerk-asset-map.edn")
    (spit "target/classes/clerk-asset-map.edn" {"/js/viewer.js" lookup-url})))

(defn package-clerk-asset-map [{:as opts :keys [target-dir]}]
  (when-not target-dir
    (throw (ex-info "target dir must be set" {:opts opts})))
  (let [asset-map @config/!asset-map]
    (spit (str target-dir java.io.File/separator "clerk-asset-map.edn") asset-map)))


(defn uber [opts]
  (b/copy-dir
   {:src-dirs   ["src/notebooks"]
    :target-dir (str class-dir "/src/notebooks")})
  (package-clerk-asset-map {:target-dir class-dir})
  (-> opts
      (merge {:lib           lib
              :version       version
              :main          'dinsro.core
              :src-dirs      ["src/dispatch"
                              "src/main"
                              "src/notebooks"
                              "src/notebook-utils"
                              "src/prod"
                              "src/shared"]
              :basis         basis
              :resource-dirs ["resources/main"]})
      (bb/uber)))
