(ns gilded-rose.core-test
  (:require [gilded-rose.core :as gilded-rose]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]))


(deftest regular-items
  (testing "quality and sell-in decreases by one before sell-in"
    (is (match? {:sell-in 9, :quality 8}
                (-> (gilded-rose/item "Elixir of the Mongoose" 10 9)
                    gilded-rose/update-item))))

  (testing "quality decreases by two after sell-in"
    (is (match? {:quality 7}
                (-> (gilded-rose/item "Elixir of the Mongoose" 0 9)
                    gilded-rose/update-item))))

  (testing "quality is never negative"
    (is (match? {:quality 0}
                (-> (gilded-rose/item "Elixir of the Mongoose" 0 0)
                    gilded-rose/update-item)))

    (is (match? {:quality 0}
                (-> (gilded-rose/item "Elixir of the Mongoose" 0 1)
                    gilded-rose/update-item)))

    (is (match? {:quality 0}
                (-> (gilded-rose/item "Elixir of the Mongoose" 10 0)
                    gilded-rose/update-item)))))
