(ns leiningen.new.ck_app
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "ck_app"))

(defn ck_app
  "Generates new Conskit app"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh ck_app project.")
    (->files data
             ["project.clj" (render "project.clj" data)]
             [".gitignore" (render ".gitignore" data)]
             ["CHANGELOG.md" (render "CHANGELOG.md" data)]
             ["LICENSE" (render "LICENSE" data)]
             ["README.md" (render "README.md" data)]
             ["Procfile" (render "Procfile" data)]
             ["Dockerfile" (render "Dockerfile" data)]
             ["dev/clj/user.clj" (render "user.clj" data)]
             ["dev/cljs/{{sanitized}}/dev.cljs" (render "dev.cljs" data)]
             ["dev-resources/bootstrap.cfg" (render "bootstrap.cfg" data)]
             ["dev-resources/config.conf" (render "config.conf" data)]
             ["dev-resources/prod.conf" (render "prod.conf" data)]
             ["dev-resources/logback-dev.xml" (render "logback-dev.xml" data)]
             ["dev-resources/logback-test.xml" (render "logback-test.xml" data)]
             ["resources/db/migration/01__create_account_table.sql" (render "01__create_account_table.sql" data)]
             ["resources/db/sql/account.sql" (render "account.sql" data)]
             ["src/clj/{{sanitized}}/core.clj" (render "core.clj" data)]
             ["src/clj/{{sanitized}}/main.clj" (render "main.clj" data)]
             ["src/clj/{{sanitized}}/services/socket.clj" (render "socket.clj" data)]
             ["src/cljs/{{sanitized}}/config.cljs" (render "config.cljs" data)]
             ["src/cljs/{{sanitized}}/core.cljs" (render "core.cljs" data)]
             ["src/cljs/{{sanitized}}/db.cljs" (render "db.cljs" data)]
             ["src/cljs/{{sanitized}}/handlers.cljs" (render "handlers.cljs" data)]
             ["src/cljs/{{sanitized}}/routing.cljs" (render "routing.cljs" data)]
             ["src/cljs/{{sanitized}}/subs.cljs" (render "subs.cljs" data)]
             ["src/cljs/{{sanitized}}/views.cljs" (render "views.cljs" data)]
             ["test/{{sanitized}}/core_test.clj" (render "core_test.clj" data)]
             )))
