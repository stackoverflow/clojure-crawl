(ns clojure-crawl.map
  (:use clojure.set
	clojure-crawl.enemies
	clojure-crawl.utils))

(def size 8)

(def *enemy-chance* 45)

(def *treasure-chance* 15)

(def current-room (atom nil))

(def current-level (atom nil))

(def dungeon (atom []))

(defrecord Level [depth rooms])

(defrecord Room [right left front rear shrine? up? down? enemy treasure pos visited])

(defn- visit-current-room []
  (reset! (:visited @current-room) true))

(defn- follow-graph
  "An algorithm to calculate if this level map is traversable"
  [visited acc level]
  (let [n (first (difference acc visited))]
    (if (not n)
      acc ;the end
      (let [nr (inc n)
            nl (dec n)
            nu (+ n size)
            nd (- n size)
            rbarrier (= (mod n size) 0)
            lbarrier (= (mod n size) 1)
            ubarrier (>= n (inc (* size (dec size))))
            dbarrier (<= n size)
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

(defn- gen-level-structure []
  (let [level (atom (set (range 1 (inc (* size size)))))
        total (atom 0)]
    (while (< @total 25)
      (let [choice (rand-nth (vec @level))]
        (when (graph-complete? (disj @level choice))
          (swap! level disj choice)
          (swap! total inc))))
    @level))

(defn- gen-level [depth]
  (let [stru (gen-level-structure)
        start (rand-nth (vec stru))
        end (rand-nth (vec (disj stru start)))
        shrine (if (= (mod depth 5) 0)
                 (rand-nth (vec (disj stru start end)))
                 nil)]
    (loop [ns stru, rooms []]
      (if ns
        (let [n (first ns)
              up? (= start n)
              down? (= end n)
              shrine? (= shrine n)
              enemy-chance (if (not up?)
                             (probability-result *enemy-chance*))
              enemy (if enemy-chance
                      (random-enemy depth)
                      nil)
              rigth (and (not= (mod n size) 0)
                      (contains? stru (inc n)))
              left (and (not= (mod n size) 1)
                     (contains? stru (dec n)))
              front (contains? stru (+ n size))
              rear (contains? stru (- n size))
              treasure (probability-result *treasure-chance*)
              room (new Room rigth left front rear shrine? up? down?
                     enemy treasure n (atom false))]
          (recur (next ns) (conj rooms room)))
        rooms))))

(defn- set-new-level [depth]
  (let [level (new Level depth (gen-level depth))]
    (swap! dungeon conj level)
    (reset! current-level level)
    (reset! current-room (first (filter :up? (:rooms level))))
    (visit-current-room)
    level))

(defn enter-dungeon []
  (let [depth (inc (count @dungeon))]
    (if (> depth 1)
      false
      (set-new-level depth))))

(defn descend []
  (if (:down? @current-room)
    (let [cdepth (:depth @current-level)
	  ddepth (count @dungeon)]
      (if (= cdepth ddepth)
	(set-new-level (inc cdepth))
	(let [level (nth @dungeon cdepth)]
	  (reset! current-level level)
	  (reset! current-room (first (filter :up? (:rooms level))))
	  (visit-current-room)
	  level)))))

(defn ascend []
  (if (:up? @current-room)
    (let [depth (:depth @current-level)]
      (if (<= depth 1)
        false
        (let [level (nth @dungeon (- depth 2))]
          (reset! current-level level)
          (reset! current-room (first (filter :down? (:rooms level))))
          (visit-current-room)
          level)))))

(defn go
  ([side]
     (cond (= side :left)
	   (go side dec)
	   (= side :right)
	   (go side inc)
	   (= side :front)
	   (go side (fn [pos] (+ pos size)))
	   (= side :rear)
	   (go side (fn [pos] (- pos size)))))
  ([side posfn]
     (when (and @current-room (side @current-room))
       (let [pos (posfn (:pos @current-room))
	     room (first (filter #(= pos (:pos %)) (:rooms @current-level)))]
	 (reset! current-room room)
	 (visit-current-room)))))

; helper
(defn- show-level [level]
  (doseq [x (range 1 (inc (* size size)))]
    (if (contains? level x)
      (print "x ")
      (print ". "))
    (when (= (mod x size) 0)
      (print "\n"))))