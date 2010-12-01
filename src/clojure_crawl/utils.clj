(ns clojure-crawl.utils)

(defn rand-between [start end]
  (let [diff (- end start)
	r (rand diff)]
    (+ r start)))

(defn add-to! [atom & items]
  (apply swap! atom conj items))

(defn eval-string [s]
  (eval (read-string s)))