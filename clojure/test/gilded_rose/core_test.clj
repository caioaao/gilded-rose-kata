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
  (testing "increase in quality over time"
    (is (match? {:sell-in 9, :quality 10}
                (-> (gilded-rose/item "any" 10 9)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))

    (is (match? {:sell-in 18, :quality 44}
                (-> (gilded-rose/item "any" 19 43)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))

    (is (match? {:quality 44}
                (-> (gilded-rose/item "any" 0 43)
                    gilded-rose/as-aged
                    gilded-rose/update-item))))

  (testing "quality never go above 50"
    (is (match? {:sell-in 9, :quality 50}
                (-> (gilded-rose/item "any" 10 49)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))

    (is (match? {:sell-in 9, :quality 50}
                (-> (gilded-rose/item "any" 10 50)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))

    (is (match? {:quality 50}
                (-> (gilded-rose/item "any" -10 50)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))

    (is (match? {:quality 50}
                (-> (gilded-rose/item "any" 0 50)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))

    (is (match? {:quality 50}
                (-> (gilded-rose/item "any" 0 49)
                    gilded-rose/as-aged
                    gilded-rose/update-item)))))

(deftest backstage-passes
  (testing "loses its quality after sell-in"
    (is (match? {:quality 0}
                (-> (gilded-rose/backstage-pass "any" 0 9)
                    gilded-rose/update-item)))
    (is (match? {:quality 0}
                (-> (gilded-rose/backstage-pass "any" 0 50)
                    gilded-rose/update-item)))
    (is (match? {:quality 0}
                (-> (gilded-rose/backstage-pass "any" -100 50)
                    gilded-rose/update-item))))

  (testing "when sell-in is between 0 and 5, increses quality by 3"
    (is (match? {:sell-in 0, :quality 12}
                (-> (gilded-rose/backstage-pass "any" 1 9)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 4, :quality 14}
                (-> (gilded-rose/backstage-pass "any" 5 11)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 3, :quality 22}
                (-> (gilded-rose/backstage-pass "any" 4 19)
                    gilded-rose/update-item))))

  (testing "when sell-in is between 5 and 10, increses quality by 2"
    (is (match? {:sell-in 7, :quality 11}
                (-> (gilded-rose/backstage-pass "any" 8 9)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 5, :quality 14}
                (-> (gilded-rose/backstage-pass "any" 6 12)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 9, :quality 22}
                (-> (gilded-rose/backstage-pass "any" 10 20)
                    gilded-rose/update-item))))

  (testing "when sell-in is above 10, increses quality by 1"
    (is (match? {:sell-in 10, :quality 11}
                (-> (gilded-rose/backstage-pass "any" 11 10)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 199, :quality 14}
                (-> (gilded-rose/backstage-pass "any" 200 13)
                    gilded-rose/update-item))))

  (testing "never goes above 50"
    (is (match? {:sell-in 10, :quality 50}
                (-> (gilded-rose/backstage-pass "any" 11 50)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 8, :quality 50}
                (-> (gilded-rose/backstage-pass "any" 9 50)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 8, :quality 50}
                (-> (gilded-rose/backstage-pass "any" 9 49)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 3, :quality 50}
                (-> (gilded-rose/backstage-pass "any" 4 50)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 3, :quality 50}
                (-> (gilded-rose/backstage-pass "any" 4 49)
                    gilded-rose/update-item)))

    (is (match? {:sell-in 3, :quality 50}
                (-> (gilded-rose/backstage-pass "any" 4 48)
                    gilded-rose/update-item)))))

(deftest conjured-items
  (testing "any item can be conjured without leaving its category"
    (is (let [item (gilded-rose/as-conjured (gilded-rose/sulfuras))]
          (and (gilded-rose/conjured? item)
               (gilded-rose/legendary? item))))
    (is (let [item (gilded-rose/as-conjured (gilded-rose/aged-brie 120 20))]
          (and (gilded-rose/conjured? item)
               (gilded-rose/aged? item))))
    (is (let [item (gilded-rose/as-conjured (gilded-rose/item "some" 8 20))]
          (and (gilded-rose/conjured? item)
               (gilded-rose/regular? item))))
    (is (let [item (gilded-rose/as-conjured (gilded-rose/backstage-pass "some pass" 3 4))]
          (and (gilded-rose/conjured? item)
               (gilded-rose/backstage-pass? item)))))

  (testing "doubles the quality decrease"
    (is (match? (gilded-rose/sulfuras)
                (-> (gilded-rose/sulfuras)
                    gilded-rose/as-conjured
                    gilded-rose/update-item)))

    (is (match? {:quality 21}
                (-> (gilded-rose/aged-brie 120 20)
                    gilded-rose/as-conjured
                    gilded-rose/update-item)))

    (is (match? {:sell-in 7
                 :quality 18}
                (-> (gilded-rose/item "some" 8 20)
                    gilded-rose/as-conjured
                    gilded-rose/update-item)))

    (is (match? {:quality 18}
                (-> (gilded-rose/item "some" 0 22)
                    gilded-rose/as-conjured
                    gilded-rose/update-item)))

    (is (match? {:sell-in 2, :quality 7}
                (-> (gilded-rose/backstage-pass "some pass" 3 4)
                    gilded-rose/as-conjured
                    gilded-rose/update-item)))))
