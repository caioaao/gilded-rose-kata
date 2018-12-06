(ns gilded-rose.core-test
  (:require [gilded-rose.core :as gilded-rose]
            [gilded-rose.item :as item]
            [clojure.test :refer :all]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(deftest update-conforms-to-spec
  (is (match? {:result true :pass? true}
              (-> (stest/check `gilded-rose/update-quality)
                  first
                  :clojure.spec.test.check/ret))))
