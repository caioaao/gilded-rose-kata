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
                (-> (gilded-rose/item "any" 10 9)
                    gilded-rose/update-item))))

  (testing "quality decreases by two after sell-in"
    (is (match? {:quality 7}
                (-> (gilded-rose/item "some" 0 9)
                    gilded-rose/update-item))))

  (testing "quality is never negative"
    (is (match? {:quality 0}
                (-> (gilded-rose/item "random" 0 0)
                    gilded-rose/update-item)))

    (is (match? {:quality 0}
                (-> (gilded-rose/item "name" 0 1)
                    gilded-rose/update-item)))

    (is (match? {:quality 0}
                (-> (gilded-rose/item "here" 10 0)
                    gilded-rose/update-item)))))

(deftest legendary-items
  (testing "quality and sell-in never decreases"
    (is (match? {:sell-in 10, :quality 9}
                (-> (gilded-rose/item "any" 10 9)
                    gilded-rose/as-legendary
                    gilded-rose/update-item)))

    (is (match? {:quality 9}
                (-> (gilded-rose/item "some" 0 9)
                    gilded-rose/as-legendary
                    gilded-rose/update-item)))

    (is (match? {:quality 0}
                (-> (gilded-rose/item "random" 0 0)
                    gilded-rose/as-legendary
                    gilded-rose/update-item)))

    (is (match? {:quality 1}
                (-> (gilded-rose/item "name" 0 1)
                    gilded-rose/as-legendary
                    gilded-rose/update-item)))

    (is (match? {:quality 0}
                (-> (gilded-rose/item "here" 10 0)
                    gilded-rose/as-legendary
                    gilded-rose/update-item))))

  (testing "Sulfuras is always legendary and has quality of 80"
    (is (gilded-rose/legendary? (gilded-rose/sulfuras)))
    (is (= 80  (:quality (gilded-rose/sulfuras))))))

(deftest aged-items
  (testing "aged items increase in quality over time"
    (is (match? {:sell-in 9, :quality 8}
                (-> (gilded-rose/item "any" 10 9)
                    gilded-rose/update-item)))))


