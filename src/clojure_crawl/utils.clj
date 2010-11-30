(ns clojure-crawl.utils)

(defn rand-between [start end]
  (let [diff (- end start)
	r (rand diff)]
    (+ r start)))