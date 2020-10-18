pipeline {
    agent{
        kubernetes {
            yamlFile 'KubernetesPod.yaml'
        }
    }

    stages {
        stage('init') {
            steps {
                echo "Hello World"

                stash 'files'

                node(POD_LABEL) {
                    container('builder') {
                        unstash 'files'

                        sh "env | sort"

                        sh "apt update"
                        sh "apt install -y chromium"
                        sh "export CHROME_BIN=\"/usr/bin/chromium\""

                        sh "mkdir -p /dinsro/test"
                        sh "cp test-config.edn.example test-config.edn"

                        sh "npm install"
                        sh "npm install -g karma-cli"
                        sh "script/lint-kondo"
                        sh "script/test"
                        sh "script/test-javascript"
                        sh "script/build-production"
                    }
                }
            }
        }
    }
}
