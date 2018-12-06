(ns gilded-rose.item
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::sell-in pos-int?)

(s/def ::quality pos-int?)

(s/def ::item (s/keys :req-un [::name ::sell-in ::quality]))

(s/fdef item
  :args (s/cat :name ::name
               :sell-in ::sell-in
               :quality ::quality)
  :ret ::item)
(defn item [item-name, sell-in, quality]
  {:name item-name, :sell-in sell-in, :quality quality})
