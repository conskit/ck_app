# ck_app

ck_app is the official opinionated Leiningen template for [Conskit](https://github.com/conskit/conskit). It comes packaged with the following modules:

- [conskit](https://github.com/conskit/conskit)
- [ck.routing](https://github.com/conskit/ck.routing) [bidi]
- [ck.migrations](https://github.com/conskit/ck.migrations) [flyway]
- [ck.server](https://github.com/conskit/ck.server) [http-kit]
- [ck.react-server](https://github.com/conskit/ck.react-server)

## Requires
- Java 8

## Usage

```
lein new ck_app <myapp>
cd <myapp>
lein repl
...
user=> (reset)
```

Visit `localhost:8080`. Check out the [book database tutorial](https://github.com/conskit/conskit/wiki/Let's-Build-a-Book-Database-(CRUD))

## Developer Notes

In addition to the modules metioned earlier, the template makes use of the following libraries:

- [sente](https://github.com/ptaoussanis/sente) - Used to provide websocket support and is used in the inlcuded socket service located in `<myapp>.services.socket`
- [re-frame](https://github.com/Day8/re-frame) - Used along with [reagent](https://reagent-project.github.io/) as the SPA framework/pattern for the UI
- [hugsql](http://www.hugsql.org/) - Used for all aspects of persistence.
- [figwheel](https://github.com/bhauman/lein-figwheel) - Provides automatic reload/recompile of clojurescript

The `core` namespace for clojure has a single service called `service` that register's one controller with three page actions [`hello-world`, `hello-page`, `not-found`], an action that serves `javascript` [`scripts`] and an action that serializes the server routes in bidi format.

### Web Server

The template uses the _ck.server_ module along with [http-kit](www.http-kit.org) to run the web server.

### Communication and Routing

Sente and [cljs-ajax](https://github.com/JulianBirch/cljs-ajax), though mostly Sente are used for all aspects of communication between the client and the server through a websocket connection. You can call actions on the server using sente's `:send-fn` available within the atom `@<myapp>.routing/sente-client`. All thats need on the server side is an action with the `^{:socket true}` anotation:

```clojure
;; Socket Action (CLJ):
(action
  ^{:socket true}
  routes
  [data]
  (str (bidify (get-routes))))

;; Called like (ClJS):
(send-fn [:<myapp>.core/routes {}])
```

Routing can be done on the serverside or clientside. You have the choice between directly using the url: `[:a {:href "/foobar"} "Foo"]` or dispatch a built-in event: `[:a {:on-click #(re-frame/dipatch [:change-page "/foobar"])} "Foo"]`. You could even do both to provide a progressively enhanced link. For your convenience there's a `link-to-page` helper function/component in the `<myapp>.routing` clojurescript namespace that takes care of this: `[link-to-page "/foobar" "Foo"]`.

Both methodologies will call the same action for which the route is assigned but the event dispatch case the page is updated in place rather than refetching the html its referenced assets. You can learn more about how this works in the documentation for [ck.react-server](https://github.com/conskit/ck.react-server)

### Persistence and Migrations

For persistence, the template provides some default functionality for SQL database. You'll have to add support for other persistence mechanisms yourself. The template takes the stance that persistence abstractions/ORMs do not add much value and as such both Migrations and Queries are written in plain SQL managed by [FlywayDb](https://flywaydb.org/) and hugsql respectively. Examples can be found in the `resources/db` directory

### Reloaded Workflow Support

The template comes with support for the [Reloaded Workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded) out the box. Simply call `(reset)` in the REPL whenever a change is made to clojure files. Clojurescript reloading is handled by Figwheel which is also started by the `(reset)` command.

The only time you should need to restart the REPL is if you've added a new dependency to your project.

### Production Packaging

To package the application for production simply create an uberjar and run it with the config files as arguments as below:

```
lein clean
lein uberjar
...
java -jar target/<myapp>-0.1.0-SNAPSHOT-standalone.jar -b dev-resources/bootstrap.cfg -c dev-resources/prod.conf
```

This will start the application in production mode which provides serverside rendering for your react/reagent/re-frame application. Visit `localhost:8080` to view the website.

**Note**: You may need to add an `externs.js` file to your project to facilitate third-party objects that should not be munged by the clojurescript compiler.

## License

Copyright © 2016 Jason Murphy

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
