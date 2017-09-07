(ns transducers-workshop.solutions.lab02)

(defn log [& [enabled]]
  (fn [rf]
    (fn
      ([] (rf))
      ([result] (rf result))
      ([result input]
       (do
         (when enabled (println (if result (str "acc " result) "") "el" input))
         (rf result input))))))

(sequence (comp (log) (filter odd?) (map inc)) (range 10))
(sequence (comp (filter odd?) (log) (map inc)) (range 10))
(sequence (comp (filter odd?) (map inc) (log)) (range 10))
(transduce (comp (log true) (filter odd?) (map inc)) + (range 10))

(defn moving-average
  ([]
   (fn [rf]
     (let [acc (volatile! [0 0])]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([prev el]
          (vswap! acc (fn [[sum cnt]] (vector (+ el sum) (inc cnt))))
          (rf prev (/ (@acc 0) (@acc 1))))))))
  ([coll]
   (sequence (moving-average) coll)))

(def avgs
  (sequence
    (comp (map dec)
          (mapcat range)
          (filter odd?)
          (moving-average)
          (map double))
    (range)))

(take 10 avgs)
