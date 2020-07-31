(defproject jep.graal "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.2-alpha1"]
                 [black.ninia/jep "3.9.0"]]
  :profiles {:uberjar {:aot :all}
             :dev  {:plugins [[cider/cider-nrepl "0.22.4"]
                              [io.taylorwood/lein-native-image "0.3.1"]]
                    :dependencies []}}
  :main jep.graal
  :native-image {:name     "jep-graal"
                 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                 :opts     ["-H:ReflectionConfigurationFiles=native/reflect-config.json"
                            "-H:JNIConfigurationFiles=native/jni-config.json,native/primitives-config.json"
                            "--report-unsupported-elements-at-runtime"
                            "--initialize-at-build-time"
                            "--allow-incomplete-classpath"
                            "--no-server"]})
