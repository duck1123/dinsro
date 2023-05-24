(ns build
  "dinsro build script"
  (:require [clojure.tools.build.api :as b]
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

(defn uber [opts]
  (b/copy-dir
   {:src-dirs   ["src/notebooks"]
    :target-dir (str class-dir "/src/notebooks")})
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
