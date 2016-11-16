(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [bidi "1.25.1" :exclusions [ring/ring-core]]
                 [com.taoensso/sente "1.10.0"]
                 ;; client
                 [org.clojure/clojurescript "1.9.89"]
                 [reagent "0.5.1"]
                 [re-frame "0.7.0"]
                 [binaryage/devtools "0.6.1"]
                 [cljs-ajax "0.5.8"]
                 ;; Server
                 [http-kit "2.1.18"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.2.1"]
                 ;; Conskit
                 [conskit "1.0.0-rc1"]
                 [ck.routing "1.0.0-rc1" :classifier "bidi"]
                 [ck.migrations "1.0.0-rc1" :classifier "flyway"]
                 [ck.server "1.0.0" :classifier "http-kit"]
                 [ck.react-server "1.0.0-rc1"]
                 [ck.config "0.1.0"]
                 ;; DB
                 [com.h2database/h2 "1.4.192"]
                 [org.flywaydb/flyway-core "4.0.3"]
                 [com.layerware/hugsql "0.4.7"]]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :source-paths ["src/clj"]

  ;:repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  ;:figwheel {:hawk-options {:watcher :polling}}

  :profiles {:dev     {:source-paths ["dev/clj"]
                       :dependencies [[org.clojure/tools.namespace "0.2.11"]
                                      [figwheel "0.5.4-7"]
                                      [figwheel-sidecar "0.5.4-7" :exclusions [ring/ring-core]]
                                      [com.cemerick/piggieback "0.2.1"]]
                       :plugins      [[lein-figwheel "0.5.4-7"]]
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
