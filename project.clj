(defproject book-desk "0.1.0-SNAPSHOT"
  :description "Book a Desk App"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [metosin/jsonista "0.2.6"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.5.5"]]
  :jvm-opts ["-Dclojure.server.myrepl={:port,6666,:accept,clojure.core.server/repl}"]
  :main ^:skip-aot book-desk.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[nubank/matcher-combinators "3.1.1"]
                                  [ring/ring-mock "0.3.2"]]
                   :source-paths ["dev"]}})
