(ns clojure-crawl.utils)

(defn rand-between [start end]
  (let [diff (- end start)
	r (rand diff)]
    (+ r start)))

(defn add-to! [atom & items]
  (apply swap! atom conj items))

(defn eval-string [s]
  (eval (read-string s)))

(defn vec->damage [v]
  (str (first v) " - " (second v)))

(defn apply-in-vec [f v]
  (vec (map f v)))