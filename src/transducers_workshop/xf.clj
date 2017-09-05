(ns transducers-workshop.xf)

(defn merge-into
  "Merge map at key k with keys at ks."
  [k ks]
  (map (fn [m] (merge (m k) (select-keys m ks)))))

(defn update-at
  "Apply f to value at key."
  [k f]
  (map (fn [m] (update m k f))))

(defn allow-if
  "Allow item if value at (non-nil) key is truthy."
  [k]
  (filter (fn [m] (if k (m k) true))))

(defn allow-if-equal
  "Allow item if value at k is same as (non-nil) v."
  [k v]
  (filter (fn [m] (if v (= (m k) v) true))))

(defn allow-in-range
  "Allow item if val at k-min <= v <= val at k-max"
  [k-min k-max v]
  (filter (fn [m] (if v (<= (m k-min) v (m k-max)) true))))
