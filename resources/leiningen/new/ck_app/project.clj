(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [bidi "2.1.2" :exclusions [ring/ring-core]]
                 [com.taoensso/sente "1.11.0" :exclusions [org.clojure/clojure org.clojure/clojurescript]]
                 ;; client
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.1"]
                 [binaryage/devtools "0.9.4"]
                 [cljs-ajax "0.5.8"]
                 ;; Server
                 [http-kit "2.2.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.3.1"]
                 ;; Conskit
                 [conskit "1.0.0-rc1"]
                 [ck.routing "1.0.0-rc1" :classifier "bidi"]
                 [ck.migrations "1.0.0-rc1" :classifier "flyway"]
                 [ck.server "1.0.1" :classifier "http-kit"]
                 [ck.react-server "1.0.0-rc1"]
                 [ck.config "0.1.0"]
                 ;; DB
                 [com.h2database/h2 "1.4.192"]
                 [org.flywaydb/flyway-core "4.0.3"]
                 [com.layerware/hugsql "0.4.7"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :source-paths ["src/clj"]

  :profiles {:dev     {:source-paths ["dev/clj"]
                       :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                      [figwheel "0.5.13"]
                                      [figwheel-sidecar "0.5.13" :exclusions [ring/ring-core]]
                                      [com.cemerick/piggieback "0.2.1"]]
                       :plugins      [[lein-figwheel "0.5.13"]]
                       :cljsbuild    {:builds
                                      {:dev {:source-paths ["src/cljs" "dev/cljs"]
                                             :figwheel     {:on-jsload "{{name}}.core/mount-root"}
                                             :compiler     {:main                 {{name}}.core
                                                            :output-to            "resources/public/js/compiled/app.js"
                                                            :output-dir           "resources/public/js/compiled/out"
                                                            :asset-path           "js/compiled/out"
                                                            :source-map-timestamp true}}}}}
             :uberjar {:hooks     [leiningen.cljsbuild]
                       :main      {{name}}.main
                       :aot       [{{name}}.main]
                       :cljsbuild {:jar true
                                   :builds
                                        {:min {:source-paths ["src/cljs"]
                                               :compiler     {:main            {{name}}.core
                                                              :output-to       "resources/public/js/compiled/app.js"
                                                              :optimizations   :advanced
                                                              :closure-defines {
                                                                                {{name}}.config/DEBUG! false}
                                                              :pretty-print    false}}}}}}

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"])
