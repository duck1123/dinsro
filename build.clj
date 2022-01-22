(ns build
  "Dinsro build script"
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]))

(def lib 'duck1123/dinsro)
(def version (format "4.0.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:user :standard}))
(def jar-file (bb/default-jar-file lib version))

(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

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

(defn jar [_]
  (b/write-pom
   {:class-dir class-dir
    :lib       lib
    :version   version
    :basis     basis
    :src-dirs  ["src/main"]})
  (b/copy-dir
   {:src-dirs   ["src/main" "resources"]
    :target-dir class-dir})
  (b/jar
   {:class-dir class-dir
    :jar-file  jar-file}))

(defn eastwood "Run Eastwood." [opts]
  (-> opts (bb/run-task [:eastwood])))

(defn uber [opts]
  (-> opts
      (merge {:lib           lib
              :version       version
              :main          'dinsro.core
              :src-dirs      ["src/main" "resources/main"]
              :resource-dirs ["resources/main"]})
      (bb/uber)))

(defn uber2 [_opts]
  (clean nil)
  (b/copy-dir
   {:src-dirs   ["src/main" "resources/main"]
    :target-dir class-dir})
  (b/compile-clj
   {:basis     basis
    :src-dirs  ["src/main"]
    :class-dir class-dir})
  (b/uber
   {:class-dir class-dir
    :uber-file uber-file
    :basis     basis
    :main      'dinsro.core}))
