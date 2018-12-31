(defproject gilded-rose "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/spec.alpha "0.2.176"]]
  :profiles {:dev  {:dependencies   [[nubank/matcher-combinators "0.4.2"]
                                     [org.clojure/test.check "0.10.0-alpha3"]
                                     [org.clojure/tools.namespace "0.2.11"]]}
             :test {:resource-paths ["resources/test"]}})
