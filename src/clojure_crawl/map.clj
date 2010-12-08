(ns clojure-crawl.map
  (:use clojure.set))

(def *enemy-chance* 25)

(def current-room (atom nil))

(def current-level (atom nil))

(def dungeon (atom []))

(defrecord Level [start depth count])

(defrecord Room [right left front rear shrine? up? down? enemy treasure])

(defn- probability-result [n]
  (< n (inc (rand-int 100))))

;(defn- gen-map [start? rigth left front rear]
;  (let [depth (if start? 1 @(:count current-level))
;	shrine% (* *shrine-chance-multiplier* depth)
;	shrine? (probability-result shrine%)
;	down? (probability-result 10)
;	map (new Map )]))

(defn- follow-graph
  "An algorithm to calculate if this level map is traversable"
  [visited acc level]
  (let [n (first (difference acc visited))]
    (if (not n)
      acc ;the end
      (let [nr (inc n)
	    nl (dec n)
	    nu (+ n 7)
	    nd (- n 7)
	    rbarrier (= (mod n 7) 0)
	    lbarrier (= (mod n 7) 1)
	    ubarrier (>= n 50)
	    dbarrier (<= n 7)
	    r (when (and (not rbarrier)
			 (contains? level nr)
			 (not (contains? visited nr)))
		nr)
	    l (when (and (not lbarrier)
			 (contains? level nl)
			 (not (contains? visited nl)))
		nl)
	    u (when (and (not ubarrier)
			 (contains? level nu)
			 (not (contains? visited nu)))
		nu)
	    d (when (and (not dbarrier)
			 (contains? level nd)
			 (not (contains? visited nd)))
		nd)]
	(if (and (nil? r)
		 (nil? l)
		 (nil? u)
		 (nil? d))
	  (conj acc n) ;the end
	  (recur (conj visited n) (set (concat acc (remove nil? [r l u d]))) level))))))

(defn- graph-complete? [level]
  (let [res (follow-graph #{} #{(first level)} level)]
    (= (count level) (count res))))

(defn- gen-level []
  (let [level (atom (set (range 1 57)))
	total (atom 0)]
    (while (< @total 18)
      (let [choice (rand-nth (vec @level))]
	(when (graph-complete? (disj @level choice))
	  (swap! level disj choice)
	  (swap! total inc))))
    @level))

; helper
(defn- show-level [level]
  (doseq [x (range 1 57)]
    (if (contains? level x)
      (print "x ")
      (print ". "))
    (when (= (mod x 7) 0)
      (print "\n"))))